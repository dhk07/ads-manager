package whilter.ai.ads_manager.service;

import whilter.ai.ads_manager.model.PostRequest;

@FunctionalInterface
public interface InstagramHandler {
    String handle(PostRequest request, InstagramHandler next);
}

