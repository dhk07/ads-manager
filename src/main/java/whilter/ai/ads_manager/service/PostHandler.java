package whilter.ai.ads_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.model.PostRequest;

import java.util.Map;


@Component
public class PostHandler implements InstagramHandler {

    private final FacebookClient facebookClient;

    public PostHandler(FacebookClient facebookClient) {
        this.facebookClient = facebookClient;
    }

    @Override
    public void handle(PostRequest request, InstagramHandler next) {
        String accessToken = request.getAccessToken();
        String caption = request.getContent();
        String imageUrl = request.getImageUrl();

        try {
            String instaBusinessIdString = "17841473206015101";
            String response = facebookClient.createPost(instaBusinessIdString, caption, imageUrl, accessToken);
            ObjectMapper objectMapper = new ObjectMapper();
            Map jsonMap = objectMapper.readValue(response, Map.class);

            System.out.println("Container created successfully: " + jsonMap);
            String creationId = (String) jsonMap.get("id");
            String publishResponse = facebookClient.publishMedia(instaBusinessIdString, creationId, accessToken);
            System.out.println("Post created successfully on instagram: " + publishResponse);
        } catch (Exception e) {
            System.err.println("Post failed: " + e.getMessage());
        }
    }
}


