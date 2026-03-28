package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synamyk.entities.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(UUID paymentId);
    Optional<Payment> findByTransactionId(String transactionId);
    boolean existsByUserIdAndTestIdAndStatus(Long userId, Long testId, Payment.PaymentStatus status);
    List<Payment> findByStatusAndAmountAndCreatedAtAfter(
            Payment.PaymentStatus status, BigDecimal amount, LocalDateTime after);
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}