package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.client.MaatCourtDataApiClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.*;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    @Mock
    private MaatCourtDataApiClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenAValidRepId_whenGetHardshipByDetailTypeIsInvoked_thenResponseIsReturned() {
        when(maatCourtDataClient.getHardshipDetails(TEST_REP_ID, DETAIL_TYPE))
                .thenReturn(List.of(new ApiHardshipDetail()));
        maatCourtDataService.getHardshipByDetailType(TEST_REP_ID, DETAIL_TYPE);
        verify(maatCourtDataClient, times(1)).getHardshipDetails(TEST_REP_ID, DETAIL_TYPE);
    }

    @Test
    void givenCreateRequest_whenPersistHardshipIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.persistHardship(
                new ApiPersistHardshipRequest(), RequestType.CREATE
        );
        verify(maatCourtDataClient, times(1)).create(any());
    }

    @Test
    void givenUpdateRequest_whenPersistHardshipIsInvoked_thenResponseIsReturned() {
        maatCourtDataService.persistHardship(
                new ApiPersistHardshipRequest(), RequestType.UPDATE
        );
        verify(maatCourtDataClient, times(1)).update(any());
    }

    @Test
    void givenValidHardshipReviewId_whenGetHardshipIsInvoked_thenResponseIsReturned() {
        ApiFindHardshipResponse expected = new ApiFindHardshipResponse();
        when(maatCourtDataClient.getHardship(HARDSHIP_ID)).thenReturn(expected);
        maatCourtDataService.getHardship(HARDSHIP_ID);
        verify(maatCourtDataClient, times(1)).getHardship(HARDSHIP_ID);
    }
}