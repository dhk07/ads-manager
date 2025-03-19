package whilter.ai.ads_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whilter.ai.ads_manager.model.AdAccount;
import whilter.ai.ads_manager.model.ApiResponse;
import whilter.ai.ads_manager.model.InstagramCampaignRequest;
import whilter.ai.ads_manager.service.FacebookGraphApiService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/instagram-campaigns")
@RequiredArgsConstructor
public class InstagramCampaignController {

    private final FacebookGraphApiService facebookGraphApiService;

    @GetMapping
    public String getInstagramCampaignsPage(@AuthenticationPrincipal OAuth2AuthenticationToken authentication, Model model) {
        List<AdAccount> adAccounts = facebookGraphApiService.getAdAccounts(authentication);
        List<Map<String, String>> instagramAccounts = facebookGraphApiService.getInstagramBusinessAccounts(authentication);

        model.addAttribute("adAccounts", adAccounts);
        model.addAttribute("instagramAccounts", instagramAccounts);
        model.addAttribute("campaignRequest", new InstagramCampaignRequest());

        return "instagram-campaigns";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<ApiResponse> createInstagramCampaign(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @RequestBody InstagramCampaignRequest campaignRequest) {
        ApiResponse response = facebookGraphApiService.createInstagramCampaign(authentication, campaignRequest);
        return ResponseEntity.ok(response);
    }
}
