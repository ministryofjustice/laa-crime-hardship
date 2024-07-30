package uk.gov.justice.laa.crime.hardship.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.math.BigDecimal;

@HttpExchange
public interface CmaClient {

    @GetExchange("/api/internal/v1/assessment/means/fullAssessmentThreshold/{assessmentDate}")
    BigDecimal find(@PathVariable String assessmentDate);
}
