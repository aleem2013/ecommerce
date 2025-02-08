package com.ecommerce.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {
    
    private final Environment env;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(keycloakClientRegistration());
    }

    private ClientRegistration keycloakClientRegistration() {
        String clientId = env.getProperty("spring.security.oauth2.client.registration.keycloak.client-id");
        String clientSecret = env.getProperty("spring.security.oauth2.client.registration.keycloak.client-secret");
        
        if (clientId == null || clientSecret == null) {
            throw new IllegalStateException("Keycloak client configuration is missing. Please check your application.yml");
        }

        return ClientRegistration.withRegistrationId("keycloak")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "profile", "email", "roles")
            .authorizationUri(env.getProperty(
                "spring.security.oauth2.client.provider.keycloak.authorization-uri",
                "http://localhost:8080/auth/realms/ecommerce/protocol/openid-connect/auth"))
            .tokenUri(env.getProperty(
                "spring.security.oauth2.client.provider.keycloak.token-uri",
                "http://localhost:8080/auth/realms/ecommerce/protocol/openid-connect/token"))
            .userInfoUri(env.getProperty(
                "spring.security.oauth2.client.provider.keycloak.user-info-uri",
                "http://localhost:8080/auth/realms/ecommerce/protocol/openid-connect/userinfo"))
            .jwkSetUri(env.getProperty(
                "spring.security.oauth2.client.provider.keycloak.jwk-set-uri",
                "http://localhost:8080/auth/realms/ecommerce/protocol/openid-connect/certs"))
            .userNameAttributeName(IdTokenClaimNames.SUB)
            .clientName("Keycloak")
            .build();
    }
}
