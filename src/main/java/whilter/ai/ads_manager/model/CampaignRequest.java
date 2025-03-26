package whilter.ai.ads_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignRequest {
    private String campaignName;
    private String objective;
    private String status;
    private List<String> specialAdCategories;
}
