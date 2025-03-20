package whilter.ai.ads_manager.service;

import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.model.PostRequest;


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

        try {
            String response = facebookClient.createPost(caption, accessToken);
            System.out.println("Post successful: " + response);
        } catch (Exception e) {
            System.err.println("Post failed: " + e.getMessage());
        }
    }
}


