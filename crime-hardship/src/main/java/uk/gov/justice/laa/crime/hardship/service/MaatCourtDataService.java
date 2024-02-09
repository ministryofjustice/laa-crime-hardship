package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.enums.RequestType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataService {

    @Qualifier("maatApiClient")
    private final RestAPIClient maatAPIClient;
    private final ServicesConfiguration configuration;
    private static final String RESPONSE_STRING = "Response from Court Data API: %s";

    public List<ApiHardshipDetail> getHardshipByDetailType(Integer repId, String detailType) {

        List<ApiHardshipDetail> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getHardshipEndpoints().getHardshipDetailUrl(),
                repId,
                detailType
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public ApiPersistHardshipResponse persistHardship(ApiPersistHardshipRequest request,
                                                      RequestType requestType) {

        ApiPersistHardshipResponse response;
        if (requestType == RequestType.CREATE) {
            response = maatAPIClient.post(
                    request,
                    new ParameterizedTypeReference<>() {
                    },
                    configuration.getMaatApi().getHardshipEndpoints().getPersistHardshipUrl(),
                    Map.of()
            );
        } else {
            response = maatAPIClient.put(
                    request,
                    new ParameterizedTypeReference<>() {
                    },
                    configuration.getMaatApi().getHardshipEndpoints().getPersistHardshipUrl(),
                    Map.of()
            );
        }

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public ApiFindHardshipResponse getHardship(Integer hardshipReviewId) {
        ApiFindHardshipResponse response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {},
                configuration.getMaatApi().getHardshipEndpoints().getHardshipUrl(),
                hardshipReviewId
        );

        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public FinancialAssessmentDTO getFinancialAssessment(Integer financialAssessmentId) {
        FinancialAssessmentDTO response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {},
                configuration.getMaatApi().getFinancialAssessmentEndpoints().getSearchUrl(),
                financialAssessmentId
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }

    public void patchHardship(Integer hardshipReviewId, Map<String, Object> updateFields) {
        maatAPIClient.patch(
                updateFields,
                new ParameterizedTypeReference<>() {},
                configuration.getMaatApi().getHardshipEndpoints().getHardshipUrl(),
                Map.of(),
                hardshipReviewId
        );
    }
}
