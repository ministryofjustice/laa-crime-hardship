package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.hardship.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaatCourtDataServiceTest {

    private static final String LAA_TRANSACTION_ID = "laaTransactionId";

    @Mock
    private RestAPIClient maatCourtDataClient;

    @InjectMocks
    private MaatCourtDataService maatCourtDataService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidRepId_whenGetRepOrderCapitalByRepIdIsInvoked_thenResponseIsReturned() {
        List<HardshipReviewDetail> expected = List.of(new HardshipReviewDetail());
        when(maatCourtDataClient.get(any(), anyString(), anyMap(), anyInt(), anyString()))
                .thenReturn(expected);
        maatCourtDataService.getHardshipByDetailType(TestModelDataBuilder.TEST_REP_ID, TestModelDataBuilder.DETAIL_TYPE, LAA_TRANSACTION_ID);
        verify(maatCourtDataClient, atLeastOnce()).get(any(), anyString(), anyMap(), anyInt(), anyString());
    }
}