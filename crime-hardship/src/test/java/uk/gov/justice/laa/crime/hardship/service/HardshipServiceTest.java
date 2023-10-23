package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.common.Constants;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.PersistHardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class HardshipServiceTest {

    @InjectMocks
    private HardshipService hardshipService;

    @Spy
    private PersistHardshipMapper mapper;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private HardshipCalculationService calculationService;

    private static final HardshipResult HARDSHIP_RESULT =
            TestModelDataBuilder.getHardshipResult(HardshipReviewResult.PASS);

    private HardshipReviewDTO reviewDTO;

    @Test
    void givenValidParameters_whenCreateIsInvoked_thenHardshipIsPersisted() {
        setUpPersistence();
        when(calculationService.calculateHardship(any(HardshipReview.class), any(BigDecimal.class)))
                .thenReturn(HARDSHIP_RESULT);
        HardshipReviewDTO result = hardshipService.create(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertResult(result);
    }

    @Test
    void givenValidParameters_whenUpdateIsInvoked_thenHardshipIsUpdated() {
        setUpPersistence();
        when(calculationService.calculateHardship(any(HardshipReview.class), any(BigDecimal.class)))
                .thenReturn(HARDSHIP_RESULT);
        HardshipReviewDTO result = hardshipService.update(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertResult(result);
    }

    @Test
    void givenValidHardshipReviewId_whenFindIsInvoked_thenHardshipIsRetrieved() {
        ApiFindHardshipResponse expected =  new ApiFindHardshipResponse();
        when(maatCourtDataService.getHardship(anyInt(), anyString())).thenReturn(expected);

        hardshipService.find(TestModelDataBuilder.HARDSHIP_ID, Constants.LAA_TRANSACTION_ID);

        verify(maatCourtDataService).getHardship(anyInt(), anyString());
    }

    @Test
    void givenValidParameters_whenRollbackIsInvoked_thenHardshipStatusIsInProgressAndResultIsNull() {
        setUpPersistence();
        reviewDTO.setHardshipResult(HardshipResult.builder().result(HardshipReviewResult.PASS).build());
        HardshipReviewDTO result = hardshipService.rollback(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertThat(result.getHardshipResult().getResult()).isNull();
        assertThat(result.getHardshipMetadata().getReviewStatus()).isEqualTo(HardshipReviewStatus.IN_PROGRESS);
    }

    @Test
    void givenValidParametersWithNullHardshipResult_whenRollbackIsInvoked_thenHardshipStatusIsInProgressAndResultIsNull() {
        setUpPersistence();
        HardshipReviewDTO result = hardshipService.rollback(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertThat(result.getHardshipResult()).isNull();
        assertThat(result.getHardshipMetadata().getReviewStatus()).isEqualTo(HardshipReviewStatus.IN_PROGRESS);
    }

    private static void assertResult(HardshipReviewDTO result) {
        assertThat(result.getHardshipResult()).isEqualTo(HARDSHIP_RESULT);
    }

    private void setUpPersistence(){
        reviewDTO = TestModelDataBuilder.getHardshipReviewDTO();

        when(maatCourtDataService.persistHardship(any(ApiPersistHardshipRequest.class), anyString(),any(RequestType.class)))
                .thenReturn(TestModelDataBuilder.getApiPersistHardshipResponse());
    }
}
