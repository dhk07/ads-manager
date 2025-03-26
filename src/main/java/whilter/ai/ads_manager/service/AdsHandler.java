package whilter.ai.ads_manager.service;

import whilter.ai.ads_manager.model.AdCreative;
import whilter.ai.ads_manager.model.AdRequest;
import whilter.ai.ads_manager.model.AdSetRequest;
import whilter.ai.ads_manager.model.Campaign;
import whilter.ai.ads_manager.model.CampaignRequest;
import whilter.ai.ads_manager.model.CampaignResponse;

public interface AdsHandler {
    String handle(Campaign request, InstagramHandler next);
    CampaignResponse callCreateCampaign(CampaignRequest request);
    CampaignResponse callCreateAdSets(AdSetRequest request, String campaignId);
    CampaignResponse callCreateAdCreative(AdCreative request);
    CampaignResponse callCreateAd(AdRequest request, String adSetId);
}
