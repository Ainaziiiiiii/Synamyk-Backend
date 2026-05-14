package synamyk.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InitPaymentResponse {
    /** Pass this as `requestId` in CreateItemHandlerWidget. */
    private UUID paymentId;
    /** Pass this as `amount` (FixedAmount) in CreateItemHandlerWidget. */
    private BigDecimal amount;
    /** Pass this as `nameEn` in CreateItemHandlerWidget. */
    private String nameEn;
    /** Pass this as `callbackUrl` in CreateItemHandlerWidget. */
    private String callbackUrl;
}
