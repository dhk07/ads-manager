package whilter.ai.ads_manager.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebookClient", url = "https://graph.facebook.com/v22.0")
public interface FacebookClient {

    @PostMapping("/{igUserId}/media")
    String createPost(@PathVariable("igUserId") String igUserId,
                      @RequestParam("caption") String caption,
                      @RequestParam("image_url") String imageUrl,
                      @RequestParam("access_token") String accessToken);

    @PostMapping("/{igUserId}/media_publish")
    String publishMedia(@PathVariable("igUserId") String igUserId,
                        @RequestParam("creation_id") String creationId,
                        @RequestParam("access_token") String accessToken);
}

