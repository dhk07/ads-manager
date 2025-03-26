package whilter.ai.ads_manager.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import whilter.ai.ads_manager.model.CampaignResponse;
import whilter.ai.ads_manager.model.FacebookInterestResponse;

import java.util.Map;

@FeignClient(name = "facebookClient", url = "https://graph.facebook.com/v22.0")
public interface FacebookClient {

    @PostMapping("/{igUserId}/media")
    Map<String, Object> createPost(@PathVariable("igUserId") String igUserId,
                                   @RequestBody Map<String, Object> requestBody);

    @PostMapping("/{igUserId}/media")
    Map<String, Object> createVideoPost(@PathVariable("igUserId") String igUserId,
                                        @RequestBody Map<String, Object> requestBody);

    @PostMapping("/{igUserId}/media_publish")
    Map<String, Object> publishMedia(@PathVariable("igUserId") String igUserId,
                                     @RequestBody Map<String, Object> requestBody);

    @GetMapping("/{mediaId}?fields=status_code")
    Map<String, Object> getMediaStatus(@PathVariable("mediaId") String mediaId,
                                       @RequestHeader("Authorization") String accessToken);

    @PostMapping("/act_{adAccountId}/campaigns")
    CampaignResponse createCampaign(@PathVariable("adAccountId") String adAccountId,
                                    @RequestBody Map<String, Object> requestBody);

    @PostMapping("/act_{adAccountId}/adsets")
    CampaignResponse createAdSet(@PathVariable("adAccountId") String adAccountId,
                                       @RequestBody Map<String, Object> requestBody);

    @PostMapping("/act_{ad_account_id}/adcreatives")
    CampaignResponse createAdCreative(@PathVariable("ad_account_id") String adAccountId,
                                         @RequestBody Map<String, Object> requestBody);

    @PostMapping("/act_{adAccountId}/ads")
    CampaignResponse createAd(@PathVariable("adAccountId") String adAccountId,
                                    @RequestBody Map<String, Object> requestBody);

    @GetMapping("/search")
    FacebookInterestResponse getInterestIds(@RequestParam("type") String type,
                                            @RequestParam("q") String query,
                                            @RequestParam("access_token") String accessToken);


}

