package uk.gov.justice.laa.crime.hardship.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@HttpExchange()
public interface MaatCourtDataApiClient {
    @GetExchange("/api/internal/v1/assessment/hardship/repId/{repId}/detailType/{detailType}")
    List<ApiHardshipDetail> getHardshipDetails(@PathVariable Integer repId, @PathVariable String detailType);

    @PostExchange("/api/internal/v1/assessment/hardship")
    ApiPersistHardshipResponse create(@RequestBody ApiPersistHardshipRequest request);

    @PutExchange("/api/internal/v1/assessment/hardship")
    ApiPersistHardshipResponse update(@RequestBody ApiPersistHardshipRequest request);

    @GetExchange("/api/internal/v1/assessment/hardship/{hardshipId}")
    ApiFindHardshipResponse getHardship(@PathVariable Integer hardshipId);

    @GetExchange("/api/internal/v1/assessment/financial-assessments/{financialAssessmentId}")
    FinancialAssessmentDTO getFinancialAssessment(@PathVariable Integer financialAssessmentId);

    @PatchExchange("/api/internal/v1/assessment/hardship/{hardshipId}")
    void patchHardship(@PathVariable Integer hardshipId, @RequestBody Map<String, Object> updateFields);

}
