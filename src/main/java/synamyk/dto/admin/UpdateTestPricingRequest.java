package synamyk.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateTestPricingRequest {
    @NotNull
    private BigDecimal price;
    /**
     * IDs of sub-tests that should be marked as paid.
     * All other sub-tests of this test will be marked as free.
     */
    @NotNull
    private List<Long> paidSubTestIds;
}