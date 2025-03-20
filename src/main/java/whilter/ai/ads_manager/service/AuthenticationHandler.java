package whilter.ai.ads_manager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import whilter.ai.ads_manager.model.OAuthToken;
import whilter.ai.ads_manager.model.PostRequest;
import whilter.ai.ads_manager.repository.OAuthTokenRepository;

@Component
public class AuthenticationHandler implements InstagramHandler {

    private final OAuthTokenRepository tokenRepository;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String accessToken;

    public AuthenticationHandler(OAuthTokenRepository tokenRepository, OAuth2AuthorizedClientService authorizedClientService) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void handle(PostRequest request, InstagramHandler next) {
//        OAuthToken token = tokenRepository.findByProvider("facebook")
//                .orElseThrow(() -> new RuntimeException("No valid access token found"));
//
//        request.setAccessToken(token.getAccessToken());
        request.setAccessToken(accessToken);
        if (next != null) {
            next.handle(request, null);
        }
    }
}

