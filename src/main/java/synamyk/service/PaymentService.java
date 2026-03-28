package synamyk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synamyk.dto.CreatePaymentRequest;
import synamyk.dto.CreatePaymentResponse;
import synamyk.dto.WebhookData;
import synamyk.entities.Payment;
import synamyk.entities.Test;
import synamyk.entities.User;
import synamyk.entities.UserTestAccess;
import synamyk.repo.PaymentRepository;
import synamyk.repo.TestRepository;
import synamyk.repo.UserRepository;
import synamyk.repo.UserTestAccessRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final UserTestAccessRepository accessRepository;
    private final FinikPaymentService finikPaymentService;

    @Transactional
    public CreatePaymentResponse createPayment(Long userId, Long testId, String redirectUrl) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found"));

        if (accessRepository.existsByUserIdAndTestId(userId, testId)) {
            throw new RuntimeException("Test already purchased.");
        }

        if (paymentRepository.existsByUserIdAndTestIdAndStatus(userId, testId, Payment.PaymentStatus.COMPLETED)) {
            throw new RuntimeException("Test already paid.");
        }

        Payment payment = Payment.builder()
                .user(user)
                .test(test)
                .paymentId(UUID.randomUUID())
                .amount(test.getPrice())
                .status(Payment.PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment record created: paymentId={}, userId={}, testId={}", payment.getPaymentId(), userId, testId);

        try {
            String description = "Оплата теста: " + test.getTitle();
            String paymentUrl = finikPaymentService.createPayment(
                    payment.getPaymentId(), payment.getAmount(), description, redirectUrl);

            payment.setPaymentUrl(paymentUrl);
            paymentRepository.save(payment);

            return new CreatePaymentResponse(payment.getPaymentId(), paymentUrl, Payment.PaymentStatus.PENDING.name());

        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
            log.error("Failed to create payment in Finik: paymentId={}", payment.getPaymentId(), e);
            throw e;
        }
    }

    @Transactional
    public void processWebhook(WebhookData webhookData, String rawJson) {
        String transactionId = webhookData.getTransactionId();
        log.info("Processing webhook: transactionId={}, status={}", transactionId, webhookData.getStatus());

        if (paymentRepository.findByTransactionId(transactionId).isPresent()) {
            log.info("Webhook already processed: {}", transactionId);
            return;
        }

        if (!"SUCCEEDED".equals(webhookData.getStatus())) {
            log.warn("Unexpected webhook status: {}", webhookData.getStatus());
            return;
        }

        Payment payment = findPaymentForWebhook(webhookData);
        if (payment == null) {
            log.error("Payment not found for webhook: transactionId={}", transactionId);
            return;
        }

        if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
            log.warn("Payment already completed: paymentId={}", payment.getPaymentId());
            return;
        }

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId(transactionId);
        payment.setReceiptNumber(webhookData.getReceiptNumber());
        payment.setWebhookData(rawJson);
        paymentRepository.save(payment);

        grantTestAccess(payment.getUser(), payment.getTest());

        log.info("Payment completed: paymentId={}, userId={}, testId={}",
                payment.getPaymentId(), payment.getUser().getId(), payment.getTest().getId());
    }

    private Payment findPaymentForWebhook(WebhookData webhookData) {
        BigDecimal amount = webhookData.getAmount();
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);

        List<Payment> candidates = paymentRepository.findByStatusAndAmountAndCreatedAtAfter(
                Payment.PaymentStatus.PENDING, amount, thirtyMinutesAgo);

        if (candidates.isEmpty()) return null;
        if (candidates.size() == 1) return candidates.get(0);

        log.warn("Multiple PENDING payments found ({}): taking oldest", candidates.size());
        return candidates.stream().min(Comparator.comparing(Payment::getCreatedAt)).orElse(null);
    }

    @Transactional
    protected void grantTestAccess(User user, Test test) {
        if (accessRepository.existsByUserIdAndTestId(user.getId(), test.getId())) {
            log.info("Access already exists: userId={}, testId={}", user.getId(), test.getId());
            return;
        }

        UserTestAccess access = UserTestAccess.builder()
                .user(user)
                .test(test)
                .grantedAt(LocalDateTime.now())
                .build();
        accessRepository.save(access);

        log.info("Test access granted: userId={}, testId={}", user.getId(), test.getId());
    }

    public CreatePaymentResponse getPaymentStatus(UUID paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return new CreatePaymentResponse(payment.getPaymentId(), payment.getPaymentUrl(), payment.getStatus().name());
    }
}