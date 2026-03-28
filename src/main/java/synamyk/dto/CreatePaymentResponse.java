package synamyk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CreatePaymentResponse {
    private UUID paymentId;
    private String paymentUrl;
    private String status;
}