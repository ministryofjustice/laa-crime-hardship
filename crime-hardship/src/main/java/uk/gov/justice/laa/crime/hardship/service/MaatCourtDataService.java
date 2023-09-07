package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.hardship.common.Constants;
import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;

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

    public List<ApiHardshipDetail> getHardshipByDetailType(Integer repId, String detailType, String laaTransactionId) {

        List<ApiHardshipDetail> response = maatAPIClient.get(
                new ParameterizedTypeReference<>() {
                },
                configuration.getMaatApi().getHardshipEndpoints().getHardshipDetailUrl(),
                Map.of(Constants.LAA_TRANSACTION_ID, laaTransactionId),
                repId,
                detailType
        );
        log.info(String.format(RESPONSE_STRING, response));
        return response;
    }
}
