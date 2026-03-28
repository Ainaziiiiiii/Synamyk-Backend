package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.CreatePaymentRequest;
import synamyk.dto.CreatePaymentResponse;
import synamyk.entities.User;
import synamyk.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Finik payment endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create a Finik payment to unlock all paid sub-tests of a test.
     */
    @PostMapping("/create")
    @Operation(summary = "Create payment to unlock paid sub-tests")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            CreatePaymentResponse response = paymentService.createPayment(
                    user.getId(), request.getTestId(), request.getRedirectUrl());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Payment error: {}", e.getMessage());
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            log.error("Unexpected payment error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Check payment status by paymentId.
     */
    @GetMapping("/{paymentId}")
    @Operation(summary = "Check payment status")
    public ResponseEntity<CreatePaymentResponse> getPaymentStatus(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(UUID.fromString(paymentId)));
    }
}