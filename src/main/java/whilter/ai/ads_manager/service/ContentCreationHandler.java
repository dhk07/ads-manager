package whilter.ai.ads_manager.service;

import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.model.PostRequest;

@Component
public class ContentCreationHandler implements InstagramHandler {

    @Override
    public void handle(PostRequest request, InstagramHandler next) {
        if (request.getAccessToken() == null) {
            throw new RuntimeException("No access token available");
        }

        String content = request.getContent();
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content is empty");
        }

        if (next != null) {
            next.handle(request, null);
        }
    }
}

