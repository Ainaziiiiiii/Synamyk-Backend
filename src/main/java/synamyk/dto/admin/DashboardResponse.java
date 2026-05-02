package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private long activeUsers;
    private long completedTests;
    private long newRegistrations;
    private BigDecimal weeklyRevenue;

    private List<ActivityBar> activityChart;
    private List<SuccessBar> successChart;

    @Data
    @Builder
    public static class ActivityBar {
        private String label;
        private long count;
    }

    @Data
    @Builder
    public static class SuccessBar {
        private String subject;
        private double successRate;
    }
}
