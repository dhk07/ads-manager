package whilter.ai.ads_manager.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import whilter.ai.ads_manager.model.AdAccount;
import whilter.ai.ads_manager.model.ApiResponse;
import whilter.ai.ads_manager.model.Campaign;
import whilter.ai.ads_manager.model.CampaignRequest;
import whilter.ai.ads_manager.model.InstagramCampaignRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacebookGraphApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OAuth2AuthorizedClientService clientService;

    @Value("${facebook.api.base-url}")
    private String apiBaseUrl;

    @Value("${facebook.api.graph-version}")
    private String graphVersion;

    public List<Map<String, String>> getInstagramBusinessAccounts(OAuth2AuthenticationToken authentication) {
        String accessToken = getAccessToken(authentication);

        // First get Facebook Pages the user has access to
        String pagesUrl = buildUrl("/me/accounts");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String pagesResponse = restTemplate.exchange(
                pagesUrl,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();

        List<Map<String, String>> instagramAccounts = new ArrayList<>();

        try {
            JsonNode pagesRoot = objectMapper.readTree(pagesResponse);
            JsonNode pagesData = pagesRoot.get("data");

            if (pagesData != null && pagesData.isArray()) {
                for (JsonNode page : pagesData) {
                    String pageId = page.get("id").asText();

                    // Now get Instagram Business Accounts for each page
                    String instagramUrl = buildUrl("/" + pageId + "?fields=instagram_business_account");

                    String instagramResponse = restTemplate.exchange(
                            instagramUrl,
                            HttpMethod.GET,
                            entity,
                            String.class
                    ).getBody();

                    JsonNode instagramRoot = objectMapper.readTree(instagramResponse);

                    if (instagramRoot.has("instagram_business_account")) {
                        JsonNode instagramAccount = instagramRoot.get("instagram_business_account");
                        String instagramId = instagramAccount.get("id").asText();

                        // Get Instagram account details
                        String accountDetailsUrl = buildUrl("/" + instagramId + "?fields=name,username,profile_picture_url");

                        String accountDetailsResponse = restTemplate.exchange(
                                accountDetailsUrl,
                                HttpMethod.GET,
                                entity,
                                String.class
                        ).getBody();

                        JsonNode accountDetails = objectMapper.readTree(accountDetailsResponse);

                        Map<String, String> accountInfo = new HashMap<>();
                        accountInfo.put("id", instagramId);
                        accountInfo.put("username", accountDetails.has("username") ? accountDetails.get("username").asText() : "");
                        accountInfo.put("name", accountDetails.has("name") ? accountDetails.get("name").asText() : "");

                        instagramAccounts.add(accountInfo);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing Instagram accounts", e);
        }

        return instagramAccounts;
    }

    public ApiResponse createInstagramCampaign(OAuth2AuthenticationToken authentication, InstagramCampaignRequest request) {
        String accessToken = getAccessToken(authentication);
        String adAccountId = request.getAdAccountId();
        ApiResponse response = new ApiResponse();

        try {
            // 1. Create Campaign
            String campaignUrl = buildUrl("/" + adAccountId + "/campaigns");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> campaignParams = new LinkedMultiValueMap<>();
            campaignParams.add("name", request.getCampaignName());
            campaignParams.add("objective", request.getObjective());
            campaignParams.add("status", request.getStatus());
            campaignParams.add("special_ad_categories", "[]");

            HttpEntity<MultiValueMap<String, String>> campaignEntity = new HttpEntity<>(campaignParams, headers);

            String campaignResponse = restTemplate.exchange(
                    campaignUrl,
                    HttpMethod.POST,
                    campaignEntity,
                    String.class
            ).getBody();

            JsonNode campaignRoot = objectMapper.readTree(campaignResponse);

            if (!campaignRoot.has("id")) {
                response.setSuccess(false);
                response.setMessage("Failed to create campaign: " + campaignRoot.toString());
                return response;
            }

            String campaignId = campaignRoot.get("id").asText();

            // 2. Create Ad Set
            String adsetUrl = buildUrl("/" + adAccountId + "/adsets");

            MultiValueMap<String, String> adsetParams = new LinkedMultiValueMap<>();
            adsetParams.add("name", request.getAdSetName());
            adsetParams.add("campaign_id", campaignId);
            adsetParams.add("optimization_goal", "REACH");
            adsetParams.add("billing_event", "IMPRESSIONS");
            adsetParams.add("bid_amount", "2");
            adsetParams.add("daily_budget", "1000");
            adsetParams.add("status", request.getStatus());

            // Targeting parameters
            String targeting = String.format(
                    "{\"geo_locations\":{\"countries\":[\"US\"]},\"age_min\":20,\"age_max\":45,\"genders\":[1,2]}");
            adsetParams.add("targeting", targeting);

            // Set schedule
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String startTime = sdf.format(request.getStartDate()) + "+0000";

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(request.getStartDate());
            calendar.add(Calendar.DAY_OF_MONTH, 7); // 7 days campaign
            String endTime = sdf.format(calendar.getTime()) + "+0000";

            adsetParams.add("start_time", startTime);
            adsetParams.add("end_time", endTime);

            HttpEntity<MultiValueMap<String, String>> adsetEntity = new HttpEntity<>(adsetParams, headers);

            String adsetResponse = restTemplate.exchange(
                    adsetUrl,
                    HttpMethod.POST,
                    adsetEntity,
                    String.class
            ).getBody();

            JsonNode adsetRoot = objectMapper.readTree(adsetResponse);

            if (!adsetRoot.has("id")) {
                response.setSuccess(false);
                response.setMessage("Failed to create ad set: " + adsetRoot.toString());
                return response;
            }

            String adsetId = adsetRoot.get("id").asText();

            // 3. Create Creative for Instagram
            String creativeUrl = buildUrl("/" + adAccountId + "/adcreatives");

            MultiValueMap<String, String> creativeParams = new LinkedMultiValueMap<>();
            creativeParams.add("name", "Instagram Creative for " + request.getCampaignName());
            creativeParams.add("object_story_spec", String.format(
                    "{\"instagram_actor_id\":\"%s\",\"link_data\":{\"call_to_action\":{\"type\":\"%s\",\"value\":{\"link\":\"%s\"}},\"link\":\"%s\",\"message\":\"%s\"}}",
                    request.getInstagramAccountId(),
                    request.getCallToActionType(),
                    request.getWebsiteUrl(),
                    request.getWebsiteUrl(),
                    request.getAdText()
            ));

            HttpEntity<MultiValueMap<String, String>> creativeEntity = new HttpEntity<>(creativeParams, headers);

            String creativeResponse = restTemplate.exchange(
                    creativeUrl,
                    HttpMethod.POST,
                    creativeEntity,
                    String.class
            ).getBody();

            JsonNode creativeRoot = objectMapper.readTree(creativeResponse);

            if (!creativeRoot.has("id")) {
                response.setSuccess(false);
                response.setMessage("Failed to create ad creative: " + creativeRoot.toString());
                return response;
            }

            String creativeId = creativeRoot.get("id").asText();

            // 4. Create Ad
            String adUrl = buildUrl("/" + adAccountId + "/ads");

            MultiValueMap<String, String> adParams = new LinkedMultiValueMap<>();
            adParams.add("name", request.getAdName());
            adParams.add("adset_id", adsetId);
            adParams.add("creative", "{\"creative_id\":\"" + creativeId + "\"}");
            adParams.add("status", request.getStatus());

            HttpEntity<MultiValueMap<String, String>> adEntity = new HttpEntity<>(adParams, headers);

            String adResponse = restTemplate.exchange(
                    adUrl,
                    HttpMethod.POST,
                    adEntity,
                    String.class
            ).getBody();

            JsonNode adRoot = objectMapper.readTree(adResponse);

            if (!adRoot.has("id")) {
                response.setSuccess(false);
                response.setMessage("Failed to create ad: " + adRoot.toString());
                return response;
            }

            String adId = adRoot.get("id").asText();

            // Successful response with all created entities
            Map<String, String> responseData = new HashMap<>();
            responseData.put("campaignId", campaignId);
            responseData.put("adsetId", adsetId);
            responseData.put("creativeId", creativeId);
            responseData.put("adId", adId);

            response.setSuccess(true);
            response.setMessage("Instagram campaign created successfully");
            response.setData(responseData);

        } catch (Exception e) {
            log.error("Error creating Instagram campaign", e);
            response.setSuccess(false);
            response.setMessage("Error creating Instagram campaign: " + e.getMessage());
        }

        return response;
    }

    public List<AdAccount> getAdAccounts(OAuth2AuthenticationToken authentication) {
        String accessToken = getAccessToken(authentication);
        String url = buildUrl("/me/adaccounts");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();

        List<AdAccount> adAccounts = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");

            if (data != null && data.isArray()) {
                for (JsonNode account : data) {
//                    AdAccount adAccount = new AdAccount();
//                    String id = account.get("id").asText();
//                    adAccount.setAdId(id);
//                    adAccount.setName(account.get("name").asText());
//                    adAccounts.add(adAccount);

                    AdAccount adAccount = AdAccount.builder()
                            .adId(account.get("id").asText())
                            .name(account.get("name").asText())
                            .build();
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing ad accounts response", e);
        }

        return adAccounts;
    }

    public ApiResponse createCampaign(OAuth2AuthenticationToken authentication, CampaignRequest request) {
        String accessToken = getAccessToken(authentication);
        String adAccountId = request.getAdAccountId();
        String url = buildUrl("/" + adAccountId + "/campaigns");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", request.getName());
        map.add("objective", request.getObjective());
        map.add("status", request.getStatus());
        map.add("special_ad_categories", "[]");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        String response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        ).getBody();

        ApiResponse apiResponse = new ApiResponse();

        try {
            JsonNode root = objectMapper.readTree(response);
            if (root.has("id")) {
                apiResponse.setSuccess(true);
                apiResponse.setMessage("Campaign created successfully");
                apiResponse.setData(root.get("id").asText());
            } else {
                apiResponse.setSuccess(false);
                apiResponse.setMessage("Failed to create campaign");
            }
        } catch (JsonProcessingException e) {
            log.error("Error parsing campaign creation response", e);
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error processing response: " + e.getMessage());
        }

        return apiResponse;
    }

    public Campaign getCampaign(OAuth2AuthenticationToken authentication, String campaignId) {
        String accessToken = getAccessToken(authentication);
        String url = buildUrl("/" + campaignId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();

        Campaign campaign = new Campaign();

        try {
            JsonNode root = objectMapper.readTree(response);
            campaign.setId(root.get("id").asText());
            campaign.setName(root.get("name").asText());
            campaign.setObjective(root.has("objective") ? root.get("objective").asText() : "");
            campaign.setStatus(root.has("status") ? root.get("status").asText() : "");
        } catch (JsonProcessingException e) {
            log.error("Error parsing campaign details", e);
        }

        return campaign;
    }

    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        return client.getAccessToken().getTokenValue();
    }

    private String buildUrl(String endpoint) {
        return UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .pathSegment(graphVersion)
                .path(endpoint)
                .build()
                .toUriString();
    }
}
