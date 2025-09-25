package whilter.ai.ads_manager.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdSetRequest {
    private String name;
//    private String campaignId;
    private long dailyBudget;

    private String billingEvent;

    private int bidAmount;

    private String bidStrategy;

    @JsonProperty("optimization_goal")
    private String optimizationGoal;

    private String status;

    private Targeting targeting;

    @JsonProperty("is_advantage_plus_audience")
    private boolean isAdvantagePlusAudience;

    private String startTime;

    private String endTime;
}