package synamyk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import synamyk.config.FinikConfig;
import synamyk.util.FinikSignatureUtil;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinikPaymentService {

    private final FinikConfig config;
    private final FinikSignatureUtil signatureUtil;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public String createPayment(UUID paymentId, BigDecimal amount, String description, String redirectUrl)
            throws Exception {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Amount", amount.intValue());
        requestBody.put("CardType", "FINIK_QR");
        requestBody.put("PaymentId", paymentId.toString());
        requestBody.put("RedirectUrl", redirectUrl);

        Map<String, Object> data = new HashMap<>();
        data.put("accountId", config.getAccountId());
        data.put("merchantCategoryCode", "0742");
        data.put("name_en", truncate(description, 50));
        data.put("description", description);
        data.put("webhookUrl", config.getWebhookUrl());
        requestBody.put("Data", data);

        String timestamp = String.valueOf(System.currentTimeMillis());
        URI uri = URI.create(config.getBaseUrl() + "/v1/payment");

        Map<String, String> headers = new HashMap<>();
        headers.put("Host", uri.getHost());
        headers.put("x-api-key", config.getApiKey());
        headers.put("x-api-timestamp", timestamp);

        String signature = signatureUtil.generateSignature(
                "POST", "/v1/payment", headers, null, requestBody, config.getPrivateKeyPath());

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        String url = config.getBaseUrl() + "/v1/payment";

        log.info("Sending payment request: paymentId={}, amount={}", paymentId, amount);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("x-api-key", config.getApiKey());
        httpHeaders.set("x-api-timestamp", timestamp);
        httpHeaders.set("signature", signature);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, httpHeaders);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.FOUND) {
                String location = response.getHeaders().getLocation().toString();
                log.info("Payment URL received: {}", location);
                return location;
            }

            throw new RuntimeException("Unexpected response from Finik: " + response.getStatusCode());

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Finik error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Finik payment creation failed: " + e.getResponseBodyAsString(), e);
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}