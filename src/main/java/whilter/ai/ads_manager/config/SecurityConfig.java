package whilter.ai.ads_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityConfig(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**","/login", "/css/**", "/js/**", "/oauth2/callback/*").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizedClientService(authorizedClientService)
                        .successHandler((request, response, authentication) -> {
//                            OAuth2User user = (OAuth2User) authentication.getPrincipal();
                            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

                            if ("facebook".equalsIgnoreCase(registrationId)) {
                                response.sendRedirect("/oauth2/callback/facebook");
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/unauthorized")
                );
        return http.build();
    }
}

