package whilter.ai.ads_manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import whilter.ai.ads_manager.model.AdAccount;
import whilter.ai.ads_manager.model.ApiResponse;
import whilter.ai.ads_manager.model.Campaign;
import whilter.ai.ads_manager.model.CampaignRequest;
import whilter.ai.ads_manager.service.FacebookGraphApiService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/campaigns")
public class CampaignController {

    private final FacebookGraphApiService facebookGraphApiService;

    public CampaignController(FacebookGraphApiService facebookGraphApiService) {
        this.facebookGraphApiService = facebookGraphApiService;
    }

    @GetMapping
    public String getCampaignsPage(@AuthenticationPrincipal OAuth2AuthenticationToken authentication, Model model) {
        log.info("Getting campaigns page");
        List<AdAccount> adAccounts = facebookGraphApiService.getAdAccounts(authentication);
        model.addAttribute("adAccounts", adAccounts);
        model.addAttribute("campaignRequest", new CampaignRequest());
        return "campaigns";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<ApiResponse> createCampaign(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @RequestBody CampaignRequest campaignRequest) {
        log.info("Creating campaign: {}", campaignRequest);
        ApiResponse response = facebookGraphApiService.createCampaign(authentication, campaignRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Campaign> getCampaign(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication,
            @PathVariable("id") String campaignId) {
        log.info("Getting campaign: {}", campaignId);
        Campaign campaign = facebookGraphApiService.getCampaign(authentication, campaignId);
        return ResponseEntity.ok(campaign);
    }
}
