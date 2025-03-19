package whilter.ai.ads_manager.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class InstagramService {

//    @Value("${facebook.client-id}")
//    private String accessToken;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public String postToInstagram(String caption, String imageUrl) {
        String containerCreationUrl = "https://graph.facebook.com/v22.0/17841473206015101/media";
        String publishUrl = "https://graph.facebook.com/v22.0/17841473206015101/media_publish";
//       businessId from curl- 592769137251519
        //        appId - 1126512495828404
//        app secretId - 85aa53ccc4d1cac1df49f69d7b49ec18
        // Step 1: Create a container for the media
        Map<String, String> params = new HashMap<>();
        params.put("image_url", imageUrl);
        params.put("caption", caption);
        params.put("access_token", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(containerCreationUrl, request, Map.class);
        String creationId = (String) response.getBody().get("id");

        // Step 2: Publish the media container
        params = new HashMap<>();
        params.put("creation_id", creationId);
        params.put("access_token", accessToken);

        request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> publishResponse = restTemplate.postForEntity(publishUrl, request, Map.class);

        return publishResponse.getBody().toString();
    }
}

