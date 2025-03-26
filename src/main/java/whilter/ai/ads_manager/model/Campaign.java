package whilter.ai.ads_manager.model;

import lombok.Data;

import java.util.List;

@Data
public class Campaign {
    private CampaignRequest campaignRequest;
    private AdSetRequest adSetRequest;
    private AdCreative adCreative;
    private AdRequest adRequest;
}
