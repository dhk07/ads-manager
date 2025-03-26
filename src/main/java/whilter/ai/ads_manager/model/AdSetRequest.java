package whilter.ai.ads_manager.model;


import lombok.Data;

@Data
public class AdSetRequest {
    private String name;
//    private String campaignId;
    private float dailyBudget;
    private String billingEvent;
    private String optimizationGoal;
    private String status;
    private Targeting targeting;
}