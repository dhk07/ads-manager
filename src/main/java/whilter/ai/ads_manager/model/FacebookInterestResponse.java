package whilter.ai.ads_manager.model;


import lombok.Data;

import java.util.List;

@Data
public class FacebookInterestResponse {
    private List<CampaignResponse> data;
}
