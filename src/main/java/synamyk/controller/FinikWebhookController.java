package synamyk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.WebhookData;
import synamyk.service.PaymentService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Finik payment webhook")
public class FinikWebhookController {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @PostMapping("/finik")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody WebhookData webhook,
            @RequestHeader Map<String, String> headers) {
        try {
            log.info("Received Finik webhook: transactionId={}, status={}",
                    webhook.getTransactionId(), webhook.getStatus());

            String rawJson = objectMapper.writeValueAsString(webhook);
            paymentService.processWebhook(webhook, rawJson);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing Finik webhook", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}