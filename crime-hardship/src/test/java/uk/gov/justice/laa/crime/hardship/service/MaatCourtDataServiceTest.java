package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    private RestAPIClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidRepId_whenGetRepOrderCapitalByRepIdIsInvoked_thenResponseIsReturned() {
        List<ApiHardshipDetail> expected = List.of(new ApiHardshipDetail());
        when(maatCourtDataClient.get(any(), anyString(), anyInt(), anyString()))
                .thenReturn(expected);
        maatCourtDataService.getHardshipByDetailType(
                TestModelDataBuilder.TEST_REP_ID, TestModelDataBuilder.DETAIL_TYPE
        );
        verify(maatCourtDataClient).get(any(), anyString(), anyInt(), anyString());
    }

    @Test
    void givenCreateRequest_whenPersistHardshipIsInvoked_thenResponseIsReturned() {
        ApiPersistHardshipResponse expected = new ApiPersistHardshipResponse();
        when(maatCourtDataClient.post(any(), any(), anyString(), anyMap()))
                .thenReturn(expected);
        maatCourtDataService.persistHardship(
                new ApiPersistHardshipRequest(), RequestType.CREATE
        );
        verify(maatCourtDataClient).post(any(), any(), anyString(), anyMap());
    }

    @Test
    void givenUpdateRequest_whenPersistHardshipIsInvoked_thenResponseIsReturned() {
        ApiPersistHardshipResponse expected = new ApiPersistHardshipResponse();
        when(maatCourtDataClient.put(any(), any(), anyString(), anyMap()))
                .thenReturn(expected);
        maatCourtDataService.persistHardship(
                new ApiPersistHardshipRequest(), RequestType.UPDATE
        );
        verify(maatCourtDataClient).put(any(), any(), anyString(), anyMap());
    }

    @Test
    void givenValidHardshipReviewId_whenGetHardshipIsInvoked_thenResponseIsReturned() {
        ApiFindHardshipResponse expected = new ApiFindHardshipResponse();
        when(maatCourtDataClient.get(any(), anyString(), anyInt())).thenReturn(expected);

        maatCourtDataService.getHardship(TestModelDataBuilder.HARDSHIP_ID);

        verify(maatCourtDataClient).get(any(), anyString(), anyInt());
    }
}