package whilter.ai.ads_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.enums.BidStrategy;
import whilter.ai.ads_manager.model.AdCreative;
import whilter.ai.ads_manager.model.AdRequest;
import whilter.ai.ads_manager.model.AdSetRequest;
import whilter.ai.ads_manager.model.Campaign;
import whilter.ai.ads_manager.model.CampaignRequest;
import whilter.ai.ads_manager.model.CampaignResponse;
import whilter.ai.ads_manager.model.FacebookInterestResponse;
import whilter.ai.ads_manager.model.GeoLocation;
import whilter.ai.ads_manager.model.Interest;
import whilter.ai.ads_manager.model.ObjectStorySpec;
import whilter.ai.ads_manager.model.Targeting;
import whilter.ai.ads_manager.model.VideoData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class CampaignHandler implements AdsHandler{

    private final AccessTokenService accessTokenService;
    private final FacebookClient facebookClient;

    @Value("${application.facebook.ad-account-id}")
    private  String adAccountId;

    public CampaignHandler(AccessTokenService accessTokenService, FacebookClient facebookClient) {
        this.accessTokenService = accessTokenService;
        this.facebookClient = facebookClient;
    }

    @Override
    public String handle(Campaign request, InstagramHandler next) {
        try {
            CampaignResponse campaignResponse = callCreateCampaign(request.getCampaignRequest());
            String campaignId = campaignResponse.getId();
            log.info("Campaign created successfully with id: {} and name: {}, createdAt: {}" , campaignId, campaignResponse.getName(), campaignResponse.getCreatedTime());
            campaignResponse = callCreateAdSets(request.getAdSetRequest(), campaignId);
            String adSetId = campaignResponse.getId();
            log.info("AdSet created successfully with id: {}" , adSetId);

            campaignResponse = callCreateAdCreative(request.getAdCreative());
            String adCreativeId = campaignResponse.getId();
            log.info("AdCreative created successfully with id: {}" , adCreativeId);

            campaignResponse = callCreateAd(request.getAdRequest(), adSetId, adCreativeId);
            String adId = campaignResponse.getId();
            log.info("Ad created successfully with id: {}" , adId);
            return "Campaign created with ID: " + campaignId + ", AdSet ID: " + adSetId + ", Ad ID: " + adId;
        } catch (Exception e) {
            log.error("Failed to create campaign: {}" , e.getMessage());
            return "Failed to create campaign: " + e.getMessage();
        }
    }

    @Override
    public CampaignResponse callCreateCampaign(CampaignRequest request) {
        Map<String, Object> campaignRequest = new HashMap<>();
//        campaignRequest.put("adAccountId", adAccountId);
        campaignRequest.put("name", request.getCampaignName());
        campaignRequest.put("objective", request.getObjective());
        campaignRequest.put("status", request.getStatus());
        campaignRequest.put("access_token", accessTokenService.getValidAccessToken("Facebook"));
        campaignRequest.put("special_ad_categories", request.getSpecialAdCategories());
        log.info("campaignRequest: {}", campaignRequest);
        CampaignResponse response = facebookClient.createCampaign(adAccountId, campaignRequest);

        Map<String, Object> campaign = facebookClient.getCampaignDetails(response.getId(),
                "Bearer " + campaignRequest.get("access_token"),
                "id,name,status,objective,created_time,effective_status");
        response.setName(campaign.get("name").toString());
        response.setObjective(campaign.get("objective").toString());
        response.setStatus(campaign.get("status").toString());
        response.setCreatedTime(campaign.get("created_time").toString());
        response.setEffectiveStatus(campaign.get("effective_status").toString());
        return response;
    }

    @Override
    public CampaignResponse callCreateAdSets(AdSetRequest request, String campaignId) {
        Map<String, Object> adSetRequest = new HashMap<>();
//        adSetRequest.put("adAccountId", adAccountId);
        adSetRequest.put("name", request.getName());
        adSetRequest.put("campaign_id", campaignId);
        adSetRequest.put("optimization_goal", request.getOptimizationGoal());
        adSetRequest.put("billing_event", request.getBillingEvent());
        adSetRequest.put("daily_budget", request.getDailyBudget());
        adSetRequest.put("access_token", accessTokenService.getValidAccessToken("Facebook"));
        adSetRequest.put("start_time", request.getStartTime());
        adSetRequest.put("end_time", request.getEndTime());
        adSetRequest.put("status", request.getStatus());
        adSetRequest.put("bid_strategy", request.getBidStrategy());
        adSetRequest.put("is_advantage_plus_audience", request.isAdvantagePlusAudience());
        adSetRequest.put("targeting", getTargetingData(request.getTargeting()));

        if(!request.getBidStrategy().equalsIgnoreCase(BidStrategy.LOWEST_COST_WITHOUT_CAP.toString()))
            adSetRequest.put("bid_amount", request.getBidAmount());

        log.info("adSetRequest: {}", adSetRequest);
        return facebookClient.createAdSet(adAccountId, adSetRequest);
    }

//    private Object getTargetingData(Targeting targeting) {
//        List<String> interstIdList = new ArrayList<>();
//        Map<String, Object> targetingRequest = new HashMap<>();
//        for (Interest interest : targeting.getInterests()) {
//            FacebookInterestResponse response = facebookClient.getInterestIds("adinterest", interest.getName(), accessTokenService.getValidAccessToken("Facebook"));
//            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
//                CampaignResponse firstInterest = response.getData().getFirst();
//                if (firstInterest.getId() != null) {
//                    interstIdList.add(firstInterest.getId());
//                }
//            }
//        }
//        targetingRequest.put("interests", interstIdList);
//        targetingRequest.put("geo_locations", targeting.getGeoLocation());
//        targetingRequest.put("age_min", targeting.getAgeMin());
//        targetingRequest.put("age_max", targeting.getAgeMax());
//        return targetingRequest;
//    }

    private Object getTargetingData(Targeting targeting) {
        List<Map<String, Object>> interestsList = new ArrayList<>();
        Map<String, Object> targetingRequest = new HashMap<>();

        if (targeting.getInterests() != null) {
            for (Interest interest : targeting.getInterests()) {
                FacebookInterestResponse response =
                        facebookClient.getInterestIds("adinterest", interest.getName(),
                                accessTokenService.getValidAccessToken("Facebook"));

                if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                    CampaignResponse firstInterest = response.getData().getFirst();
                    if (firstInterest.getId() != null) {
                        Map<String, Object> interestMap = new HashMap<>();
                        interestMap.put("id", firstInterest.getId());
                        interestMap.put("name", firstInterest.getName());
                        interestsList.add(interestMap);
                    }
                }
            }
        }
        if (!interestsList.isEmpty()) {
            targetingRequest.put("interests", interestsList);
        }
        targetingRequest.put("geo_locations", targeting.getGeoLocations());
        GeoLocation geoLocations = targeting.getGeoLocations();
        if (geoLocations == null || geoLocations.getCountries().isEmpty()) {
            geoLocations.setCountries(List.of("IN")); // default fallback
        }
        targetingRequest.put("geo_locations", geoLocations);
        if( targeting.getAgeMin() > 0)
            targetingRequest.put("age_min", targeting.getAgeMin());

        if( targeting.getAgeMax() > 0)
            targetingRequest.put("age_max", targeting.getAgeMax());

        return targetingRequest;
    }


    public CampaignResponse callCreateAdCreative(AdCreative request) {
        Map<String, Object> adCreativeRequest = new HashMap<>();
        adCreativeRequest.put("name", request.getName());
//        adCreativeRequest.put("object_story_spec", createObjectStorySpec(request.getObjectStorySpec()));
//        adCreativeRequest.put("object_story_id", request.getObjectStorySpec().getPageId()+"_"+request.getObjectStorySpec().getVideoData().getVideoId());
        adCreativeRequest.put("object_story_spec", createPromotedObject(request.getObjectStorySpec()));
        adCreativeRequest.put("access_token", accessTokenService.getValidAccessToken("Facebook"));

        log.info("adCreativeRequest: {}", adCreativeRequest);
        return facebookClient.createAdCreative(adAccountId, adCreativeRequest);
    }

    private Object createObjectStorySpec(ObjectStorySpec request) {
        Map<String, Object> objectStorySpec = new HashMap<>();
        objectStorySpec.put("page_id", request.getPageId());
        objectStorySpec.put("video_data", createVideoData(request.getVideoData()));
        return objectStorySpec;
    }

    private Object createPromotedObject(ObjectStorySpec request) {
        Map<String, Object> objectPromoted = new HashMap<>();
        Map<String, Object> objectStorySpecMap = new HashMap<>();
        objectPromoted.put("page_id", request.getPageId());
        objectPromoted.put("post_id", request.getVideoData().getVideoId());
        objectStorySpecMap.put("promoted_object", objectPromoted);
        return objectStorySpecMap;
    }


    private Object createVideoData(VideoData request) {
        Map<String, Object> videoRequest = new HashMap<>();
        videoRequest.put("video_id", request.getVideoId());
        videoRequest.put("message", request.getMessage());
        videoRequest.put("title", request.getTitle());
        videoRequest.put("image_url", request.getImageUrl());

        return videoRequest;
    }

//    public CampaignResponse callCreateAdCreativeForInstagram(AdCreative request) {
//        Map<String, Object> adCreativeRequest = new HashMap<>();
//        adCreativeRequest.put("name", request.getName());
//        adCreativeRequest.put("object_story_id", request.getObjectStorySpec());
//        adCreativeRequest.put("access_token", accessToken);
//        log.info("callCreateAdCreativeForInstagram: {}", adCreativeRequest);
//        return facebookClient.createAdCreative(adAccountId, adCreativeRequest);
//    }

    public CampaignResponse callCreateAd(AdRequest request, String adSetId, String adCreativeId) {
        Map<String, Object> adRequest = new HashMap<>();
//        adRequest.put("adAccountId", adAccountId);
        adRequest.put("name", request.getName());
        adRequest.put("adset_id", adSetId);
        adRequest.put("creative", getCreativeId(adCreativeId));
        adRequest.put("status", request.getStatus());
        adRequest.put("access_token", accessTokenService.getValidAccessToken("Facebook"));
        log.info("adRequest: {}", adRequest);
        return facebookClient.createAd(adAccountId, adRequest);
    }

    private Object getCreativeId(String creativeIds) {
        Map<String, Object> creativeRequest = new HashMap<>();
        creativeRequest.put("creative_id", creativeIds);
        return creativeRequest;
    }
}
