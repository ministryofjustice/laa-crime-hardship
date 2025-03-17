package uk.gov.justice.laa.crime.hardship.filter;

import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
public class Resilience4jRetryFilter implements ExchangeFilterFunction {

    private final Retry retry;
    private static final String DEFAULT_RETRY = "default";

    public Resilience4jRetryFilter(RetryRegistry retryRegistry, String clientName) {
        Set<String> availableRetries = retryRegistry.getAllRetries()
                .stream()
                .map(Retry::getName)
                .collect(java.util.stream.Collectors.toSet());

        String retryName = availableRetries.contains(clientName) ? clientName : DEFAULT_RETRY;
        this.retry = retryRegistry.retry(retryName);

        retry.getEventPublisher()
                .onSuccess(event ->
                                   log.info("✅ Request succeeded after {} attempts",
                                            event.getNumberOfRetryAttempts()
                                   )
                )
                .onRetry(event ->
                                 log.info("🔄 Retry #{} after {}ms for request",
                                          event.getNumberOfRetryAttempts(), event.getWaitInterval().toMillis()
                                 )
                )
                .onError(event ->
                                 log.error(
                                         "🚨 Request failed after {} retry attempts. Giving up.",
                                         event.getNumberOfRetryAttempts()
                                 )
                );
    }

    @Override
    public @NonNull Mono<ClientResponse> filter(@NonNull ClientRequest request, @NonNull ExchangeFunction next) {
        return next.exchange(request)
                .transformDeferred(RetryOperator.of(retry));
    }
}