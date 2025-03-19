package whilter.ai.ads_manager.model;

import lombok.Data;
import java.util.Date;

@Data
public class InstagramCampaignRequest {
    private String adAccountId;
    private String instagramAccountId;
    private String campaignName;
    private String adSetName;
    private String adName;
    private String objective;
    private String status;
    private String adText;
    private String websiteUrl;
    private String callToActionType;
    private Date startDate;
}
