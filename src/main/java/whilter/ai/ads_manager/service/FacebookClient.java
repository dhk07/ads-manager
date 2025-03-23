package whilter.ai.ads_manager.service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "facebookClient", url = "https://graph.facebook.com/v22.0")
public interface FacebookClient {

//    @PostMapping("/{igUserId}/media")
//    Map<String, Object>  createPost(@PathVariable("igUserId") String igUserId,
//                      @RequestParam("caption") String caption,
//                      @RequestParam("image_url") String imageUrl,
//                      @RequestParam("access_token") String accessToken);
    @PostMapping("/{igUserId}/media")
    Map<String, Object> createPost(@PathVariable("igUserId") String igUserId, @RequestBody Map<String, Object> requestBody);

//    @PostMapping("/{igUserId}/media")
//    String createVideoPost(@PathVariable("igUserId") String igUserId,
//                           @RequestParam("video_url") String videoUrl,
//                           @RequestParam("caption") String caption,
//                           @RequestParam("access_token") String accessToken);

//    @PostMapping("/{igUserId}/media_publish")
//    String publishMedia(@PathVariable("igUserId") String igUserId,
//                        @RequestParam("creation_id") String creationId,
//                        @RequestParam("access_token") String accessToken);

    @PostMapping("/{igUserId}/media")
    Map<String, Object> createVideoPost(@PathVariable("igUserId") String igUserId, @RequestBody Map<String, Object> requestBody);

    @PostMapping("/{igUserId}/media_publish")
    Map<String, Object> publishMedia(@PathVariable("igUserId") String igUserId, @RequestBody Map<String, Object> requestBody);

//    @GetMapping("/{media_id}?fields=status_code")
//    Map<String, Object> getMediaStatus(@PathVariable("media_id") String mediaId, @RequestParam String accessToken);

    @GetMapping("/{mediaId}?fields=status_code")
    Map<String, Object> getMediaStatus(@PathVariable("mediaId") String mediaId,
                                       @RequestHeader("Authorization") String accessToken);
}

