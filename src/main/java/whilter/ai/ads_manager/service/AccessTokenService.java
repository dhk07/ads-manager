package whilter.ai.ads_manager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import whilter.ai.ads_manager.model.OAuthToken;
import whilter.ai.ads_manager.repository.OAuthTokenRepository;

import java.time.Instant;
import java.util.Map;

@Service
public class AccessTokenService {

    private final OAuthTokenRepository oAuthTokenRepository;
    private final FacebookClient facebookClient;

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String facebookAppId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String facebookAppSecret;

    public AccessTokenService(OAuthTokenRepository oAuthTokenRepository, FacebookClient facebookClient) {
        this.oAuthTokenRepository = oAuthTokenRepository;
        this.facebookClient = facebookClient;
    }

    public String getValidAccessToken(String platform) {
        OAuthToken token = oAuthTokenRepository.findByProvider(platform)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            return refreshFacebookToken(token);
        }
        return token.getAccessToken();
    }

    private String refreshFacebookToken(OAuthToken token) {
        ResponseEntity<Map<String, Object>> response =
                facebookClient.exchangeForLongLivedToken("fb_exchange_token",
                        facebookAppId, facebookAppSecret, token.getAccessToken());

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String newAccessToken = (String) response.getBody().get("access_token");
            Integer expiresIn = (Integer) response.getBody().get("expires_in");

            token.setAccessToken(newAccessToken);
            token.setExpiresAt(Instant.now().plusSeconds(expiresIn));
            token.setUpdatedOn(Instant.now());
            oAuthTokenRepository.save(token);

            return newAccessToken;
        }

        throw new RuntimeException("Failed to refresh access token");
    }

}
