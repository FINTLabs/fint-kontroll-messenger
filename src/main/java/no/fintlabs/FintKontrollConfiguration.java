package no.fintlabs;

import no.fintlabs.opa.AuthorizationClient;
import no.fintlabs.opa.KontrollAuthorizationManager;
import no.fintlabs.opa.OpaApiClient;
import no.fintlabs.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import(no.fintlabs.securityconfig.FintKontrollSecurityConfig.class)
public class FintKontrollConfiguration {
    @Bean
    public KontrollAuthorizationManager kontrollAuthorizationManager() {
        return new KontrollAuthorizationManager();
    }

    @Bean
    public AuthorizationClient authorizationClient(OpaApiClient opaApiClient, AuthenticationUtil authenticationUtil) {
        return new AuthorizationClient(opaApiClient, authenticationUtil);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OpaApiClient opaApiClient(RestTemplate restTemplate) {
        return new OpaApiClient(restTemplate);
    }

    @Bean
    public AuthenticationUtil authenticationUtil() {
        return new AuthenticationUtil();
    }
}
