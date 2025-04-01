package whilter.ai.ads_manager.config;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.salt.RandomSaltGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class Beans {
    private final Environment env;
    private static final List<String> clients = List.of("facebook");

    public Beans(Environment env) {
        this.env = env;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(client -> this.getRegistration(client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {
        String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";
        String clientId = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-id");

        if (clientId == null) {
            return null;
        }

        String clientSecret = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".client-secret");

        String[] scopes = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".scope").split(",");

        String redirectUri = env.getProperty(
                CLIENT_PROPERTY_KEY + client + ".redirect-uri");
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUri)
                    .scope(scopes) // Ensure valid scopes: email, public_profile, etc.
                    .build();
        }
        return null;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {

        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository());
    }

//    @Bean("jasyptStringEncryptor") // This name is required
//    public StringEncryptor stringEncryptor() {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        config.setPassword(System.getenv("JASYPT_ENCRYPTOR_PASSWORD")); // Use env variable for security
//        config.setAlgorithm("PBEWithMD5AndDES"); // Can be changed to a stronger algorithm
//        config.setKeyObtentionIterations("1000");
//        config.setPoolSize("1");
//        config.setSaltGenerator(new RandomSaltGenerator());
//        config.setStringOutputType("base64");
//        encryptor.setConfig(config);
//        return encryptor;
//    }
}
