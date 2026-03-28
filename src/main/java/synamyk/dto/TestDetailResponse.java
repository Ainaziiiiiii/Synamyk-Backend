package synamyk.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TestDetailResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean hasPaidAccess;      // user already paid for this test
    private List<SubTestResponse> subTests;
}