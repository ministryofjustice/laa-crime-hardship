package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrimeMeansAssessmentService {

    private static final String RESPONSE_STRING = "Response from CMA API: {}";
    @Qualifier("cmaApiClient")
    private final RestAPIClient cmaApiClient;
    private final ServicesConfiguration configuration;

    public BigDecimal getFullAssessmentThreshold(LocalDateTime assessmentDate) {

        BigDecimal response = cmaApiClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getCmaApi().getCmaEndpoints().getFullAssessmentThresholdUrl(),
                assessmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
