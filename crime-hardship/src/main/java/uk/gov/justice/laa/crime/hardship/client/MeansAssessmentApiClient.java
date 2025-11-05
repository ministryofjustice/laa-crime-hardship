package uk.gov.justice.laa.crime.hardship.client;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange()
public interface MeansAssessmentApiClient {

    @GetExchange("/fullAssessmentThreshold/{assessmentDate}")
    BigDecimal find(@PathVariable String assessmentDate);
}
