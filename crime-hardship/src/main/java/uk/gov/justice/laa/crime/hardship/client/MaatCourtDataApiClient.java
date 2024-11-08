package uk.gov.justice.laa.crime.hardship.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;

import java.util.List;
import java.util.Map;

@HttpExchange()
public interface MaatCourtDataApiClient {

    @GetExchange("/hardship/{hardshipId}")
    ApiFindHardshipResponse getHardship(@PathVariable Integer hardshipId);

    @GetExchange("/hardship/repId/{repId}/detailType/{detailType}")
    List<ApiHardshipDetail> getHardshipDetails(@PathVariable Integer repId, @PathVariable String detailType);

    @PostExchange("/hardship")
    ApiPersistHardshipResponse create(@RequestBody ApiPersistHardshipRequest request);

    @PutExchange("/hardship")
    ApiPersistHardshipResponse update(@RequestBody ApiPersistHardshipRequest request);

    @PatchExchange("/hardship/{hardshipId}")
    void patchHardship(@PathVariable Integer hardshipId, @RequestBody Map<String, Object> updateFields);

    @GetExchange("/financial-assessments/{financialAssessmentId}")
    FinancialAssessmentDTO getFinancialAssessment(@PathVariable Integer financialAssessmentId);

}
