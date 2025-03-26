package whilter.ai.ads_manager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import whilter.ai.ads_manager.model.AdCreative;
import whilter.ai.ads_manager.model.AdRequest;
import whilter.ai.ads_manager.model.Campaign;
import whilter.ai.ads_manager.model.CampaignRequest;
import whilter.ai.ads_manager.model.CampaignResponse;
import whilter.ai.ads_manager.service.AdsHandler;

@RestController
@Slf4j
@RequestMapping("/ads")
public class CampaignController {

    private final AdsHandler adsHandler;

    public CampaignController(AdsHandler adsHandler) {
        this.adsHandler = adsHandler;
    }

    @PostMapping("/campaign")
    public ResponseEntity<String> createCompleteCampaign(@RequestBody Campaign request) {
        log.info("Inside createCompleteCampaign: {}", request);
        String response = adsHandler.handle(request, null);
        return ResponseEntity.ok(response);
    }

     @PostMapping("/createCampaign")
     public ResponseEntity<?> createCampaign(@RequestBody CampaignRequest request) {
         log.info("Inside createCampaign: {}", request);
         CampaignResponse response = adsHandler.callCreateCampaign(request);
         return ResponseEntity.ok(response);
     }

    @PostMapping("/createAdSet")
    public ResponseEntity<?> createAdSet(@RequestBody Campaign request, @RequestParam String campaignId) {
        log.info("Inside createAdSet: {}", request);
        CampaignResponse response = adsHandler.callCreateAdSets(request.getAdSetRequest(), campaignId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createAdCreative")
    public ResponseEntity<?> createAdCreative(@RequestBody AdCreative adCreativeRequest) {
        log.info("Inside createAdCreative: {}", adCreativeRequest);
        CampaignResponse response = adsHandler.callCreateAdCreative(adCreativeRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createAds")
    public ResponseEntity<?> createAds(@RequestBody AdRequest request, @RequestParam String adSetId) {
        log.info("Inside createAds: {}", request);
        CampaignResponse response = adsHandler.callCreateAd(request, adSetId);
        return ResponseEntity.ok(response);
    }



    // @GetMapping("/get")
    // public ResponseEntity<?> getCampaign(@RequestParam String campaignId) {
    //     log.info("Received request: {}", campaignId);
    //     CampaignRequest request = new CampaignRequest(campaignId);
    //     String response = adsHandler.handle(request, null);
    //     return ResponseEntity.ok(response);
    // }

    // @PutMapping("/update")
    // public ResponseEntity<?> updateCampaign(@RequestBody CampaignRequest request) {
    //     log.info("Received request: {}", request);
    //     String response = adsHandler.handle(request, null);
    //     return ResponseEntity.ok(response);
    // }

    // @DeleteMapping("/delete")
    // public ResponseEntity<?> deleteCampaign(@RequestParam String campaignId) {
    //     log.info("Received request: {}", campaignId);
    //     CampaignRequest request = new CampaignRequest(campaignId);
    //     String response = adsHandler.handle(request, null);
    //     return ResponseEntity.ok(response);
    // }

}
