package uk.gov.justice.laa.crime.hardship.service;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    private static final String SERVICE_NAME = "maatCourtDataService";
    private final MaatCourtDataApiClient maatCourtDataApiClient;
    private static final String RESPONSE_STRING = "Response from Court Data API: {}";
    private static final String REQUEST_STRING = "Request to Court Data API: {}";

    @Retry(name = SERVICE_NAME)
    public List<ApiHardshipDetail> getHardshipByDetailType(Integer repId, String detailType) {
        List<ApiHardshipDetail> response = maatCourtDataApiClient.getHardshipDetails(repId, detailType);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public ApiPersistHardshipResponse persistHardship(ApiPersistHardshipRequest request,
                                                      RequestType requestType) {
        log.debug(REQUEST_STRING, request);

        ApiPersistHardshipResponse response;
        if (requestType == RequestType.CREATE) {
            response = maatCourtDataApiClient.create(request);
        } else {
            response = maatCourtDataApiClient.update(request);
        }

        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public ApiFindHardshipResponse getHardship(Integer hardshipReviewId) {
        ApiFindHardshipResponse response = maatCourtDataApiClient.getHardship(hardshipReviewId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId) {
        FinancialAssessmentDTO response = maatCourtDataApiClient.getFinancialAssessment(financialAssessmentId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    @Retry(name = SERVICE_NAME)
    public void patchHardship(Integer hardshipReviewId, Map<String, Object> updateFields) {
        log.debug(REQUEST_STRING, updateFields);
        maatCourtDataApiClient.patchHardship(hardshipReviewId, updateFields);
    }
}
