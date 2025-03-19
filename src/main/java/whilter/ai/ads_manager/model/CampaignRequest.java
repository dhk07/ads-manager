package whilter.ai.ads_manager.model;
import lombok.Data;

@Data
public class CampaignRequest {
    private String adAccountId;
    private String name;
    private String objective;
    private String status;
}
