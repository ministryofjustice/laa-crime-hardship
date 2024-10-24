package uk.gov.justice.laa.crime.hardship.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.client.MeansAssessmentApiClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrimeMeansAssessmentService {

    private static final String SERVICE_NAME = "crimeMeansAssessmentService";
    private static final String RESPONSE_STRING = "Response from CMA API: {}";
    private final MeansAssessmentApiClient cmaApiClient;

    @Retry(name = SERVICE_NAME)
    public BigDecimal getFullAssessmentThreshold(LocalDateTime assessmentDate) {
        BigDecimal response = cmaApiClient.find(assessmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
