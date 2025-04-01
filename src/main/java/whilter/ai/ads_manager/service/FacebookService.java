package whilter.ai.ads_manager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.entity.FacebookProfile;
import whilter.ai.ads_manager.repository.CustomerRepository;
import whilter.ai.ads_manager.repository.FacebookProfileRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class FacebookService {

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String redirectUri;

    private final CustomerRepository customerRepository;
    private final FacebookClient facebookClient;
    private final FacebookProfileRepository facebookRepository;

    public FacebookService(CustomerRepository customerRepository, FacebookClient facebookClient, FacebookProfileRepository facebookRepository) {
        this.customerRepository = customerRepository;
        this.facebookClient = facebookClient;
        this.facebookRepository = facebookRepository;
    }

    public void linkFacebookAccount(Long userId, String authCode) {
        log.info("In linkFacebookAccount");
        Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));


        Map<String, Object> tokenResponse = facebookClient.getAccessToken(clientId, clientSecret, authCode, redirectUri);
        String accessToken = (String) tokenResponse.get("access_token");
        Integer expiresIn = (Integer) tokenResponse.get("expires_in");

        storeFacebookProfile(customer, accessToken, expiresIn);

    }

    private void storeFacebookProfile(Customer customer, String accessToken, Integer expiresIn) {
        log.info("storeFacebookProfile");

        Map<String, Object> userInfo = facebookClient.getUserInfo("id,name,email", accessToken);
        String facebookId = (String) userInfo.get("id");
        String emailId = (String) userInfo.get("email");

        // Save or update FacebookProfile
        FacebookProfile customerProfile = facebookRepository.findByCustomerId(customer.getId())
                .orElse(new FacebookProfile());

        customerProfile.setCustomer(customer);
        customerProfile.setFacebookId(facebookId);
        customerProfile.setAccessToken(accessToken);
        customerProfile.setLinkedStatus(true);
        customerProfile.setEmailId(emailId);

        // Default expiry if not provided (60 days for long-lived token)
        customerProfile.setTokenExpiry(Instant.now().plusSeconds(Objects.requireNonNullElse(expiresIn, 3600 * 24 * 10)));

//        FacebookProfile facebookProfile = facebookProfileRepository.save(profile);
        customer.setFacebookProfile(customerProfile);
        customerRepository.save(customer);
    }

    public Customer getByUserId(Long userId) {
        return customerRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }
}
