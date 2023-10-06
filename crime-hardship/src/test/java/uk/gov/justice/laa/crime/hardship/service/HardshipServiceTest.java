package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
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
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private static final HardshipResult HARDSHIP_RESULT = TestModelDataBuilder.getHardshipResult(HardshipReviewResult.PASS);

    private HardshipReviewDTO reviewDTO;

    @BeforeEach
    void setUp(){
        reviewDTO = TestModelDataBuilder.getHardshipReviewDTO();
        setUpMockForHardshipPersistence();
    }
    @Test
    void givenValidParameters_whenCreateIsInvoked_thenHardshipIsPersisted() {
        HardshipReviewDTO result = hardshipService.create(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertResult(result);
    }

    @Test
    void givenValidParameters_whenUpdateIsInvoked_thenHardshipIsUpdated() {
        HardshipReviewDTO result = hardshipService.update(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertResult(result);
    }

    private static void assertResult(HardshipReviewDTO result) {
        assertThat(result.getHardshipResult()).isEqualTo(HARDSHIP_RESULT);
    }

    private void setUpMockForHardshipPersistence() {
        when(calculationService.calculateHardship(any(HardshipReview.class), any(BigDecimal.class)))
                .thenReturn(HARDSHIP_RESULT);

        when(maatCourtDataService.persistHardship(any(ApiPersistHardshipRequest.class), anyString(),any(RequestType.class)))
                .thenReturn(TestModelDataBuilder.getApiPersistHardshipResponse());
    }
}
