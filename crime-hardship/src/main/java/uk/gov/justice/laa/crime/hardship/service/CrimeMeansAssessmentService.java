package uk.gov.justice.laa.crime.hardship.service;

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
    private final MeansAssessmentApiClient cmaApiClient;
    private static final String RESPONSE_STRING = "Response from CMA API: {}";

    public BigDecimal getFullAssessmentThreshold(LocalDateTime assessmentDate) {
        BigDecimal response = cmaApiClient.find(assessmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        log.info(RESPONSE_STRING, response);
        return response;
    }
}
