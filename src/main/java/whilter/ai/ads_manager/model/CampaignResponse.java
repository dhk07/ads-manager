package whilter.ai.ads_manager.model;

import lombok.Data;

@Data
public class CampaignResponse {
    private String id;
    private String name;
    private String status;
    private String objective;
    private String effectiveStatus;
    private String createdTime;
}
