package uk.gov.justice.laa.crime.hardship.config;

import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.gov.justice.laa.crime.hardship.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.hardship.client.MeansAssessmentApiClient;

import java.time.Duration;

@Configuration
@AllArgsConstructor
@Slf4j
public class WebClientsConfiguration {

    @Bean("maatCourtDataWebClient")
    WebClient maatCourtDataWebClient(ServicesConfiguration servicesConfiguration, ClientRegistrationRepository clientRegistrations,
                                     OAuth2AuthorizedClientRepository authorizedClients) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations, authorizedClients
                );
        oauth.setDefaultClientRegistrationId(servicesConfiguration.getMaatApi().getRegistrationId());
        return getWebClient(servicesConfiguration.getMaatApi().getBaseUrl(), oauth);
    }

    @Bean("meansAssessmentWebClient")
    WebClient meansAssessmentWebClient(ServicesConfiguration servicesConfiguration, ClientRegistrationRepository clientRegistrations,
                                       OAuth2AuthorizedClientRepository authorizedClients) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations, authorizedClients
                );
        oauth.setDefaultClientRegistrationId(servicesConfiguration.getCmaApi().getRegistrationId());
        return getWebClient(servicesConfiguration.getCmaApi().getBaseUrl(), oauth);
    }

    @Bean
    MaatCourtDataApiClient maatCourtDataApiClient(@Qualifier("maatCourtDataWebClient") WebClient maatCourtDataWebClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(WebClientAdapter.create(maatCourtDataWebClient))
                        .build();
        return httpServiceProxyFactory.createClient(MaatCourtDataApiClient.class);
    }

    @Bean
    MeansAssessmentApiClient meansAssessmentApiClient(@Qualifier("meansAssessmentWebClient") WebClient meansAssessmentApiClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(WebClientAdapter.create(meansAssessmentApiClient))
                        .build();
        return httpServiceProxyFactory.createClient(MeansAssessmentApiClient.class);
    }

    private static WebClient getWebClient(String servicesConfiguration, ServletOAuth2AuthorizedClientExchangeFilterFunction oauth) {
        ConnectionProvider provider =
                ConnectionProvider.builder("custom")
                        .maxConnections(500)
                        .maxIdleTime(Duration.ofSeconds(20))
                        .maxLifeTime(Duration.ofSeconds(60))
                        .pendingAcquireTimeout(Duration.ofSeconds(60))
                        .evictInBackground(Duration.ofSeconds(120))
                        .build();

        return WebClient.builder()
                .baseUrl(servicesConfiguration)
                .clientConnector(new ReactorClientHttpConnector(
                                HttpClient.create(provider)
                                        .resolver(DefaultAddressResolverGroup.INSTANCE)
                                        .compress(true)
                                        .responseTimeout(Duration.ofSeconds(30))
                        )
                )
                .filter(oauth)
                .build();
    }
}
