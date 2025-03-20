package whilter.ai.ads_manager.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebookClient", url = "https://graph.facebook.com")
public interface FacebookClient {

    @PostMapping("/me/media")
    String createPost(@RequestParam("caption") String caption,
                      @RequestParam("access_token") String accessToken);
}

