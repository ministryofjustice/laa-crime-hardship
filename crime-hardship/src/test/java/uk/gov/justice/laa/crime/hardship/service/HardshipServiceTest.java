package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.FULL_THRESHOLD;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class HardshipServiceTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @InjectMocks
    private HardshipService hardshipService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Test
    void givenHardshipReviewAmount_whenCalculateHardshipByDetailIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);

        List<HardshipReviewDetail> hardshipReviewDetailList =
                TestModelDataBuilder.getHardshipReviewDetailList("Y", 100);

        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);

        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void givenHardshipDetailWithZeroAmount_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);

        List<HardshipReviewDetail> hardshipReviewDetailList =
                TestModelDataBuilder.getHardshipReviewDetailList("Y", 0);

        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);

        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenHardshipDetailWithNotAccepted_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request =
                TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);

        List<HardshipReviewDetail> hardshipReviewDetailList =
                TestModelDataBuilder.getHardshipReviewDetailList("N", 10);

        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);

        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenHardshipReviewWithExtraExpenditure_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getCrownHardshipReviewWithDetails(EXPENDITURE);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(-3320.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.PASS);
    }

    @Test
    void givenHardshipReviewWithDeniedIncome_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getCrownHardshipReviewWithDetails(INCOME);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(3000.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.PASS);
    }

    @Test
    void givenHardshipReviewWithSolicitorCosts_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getMagsHardshipReviewWithDetails(SOL_COSTS);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(-5625.0).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.PASS);
    }


    @Test
    void givenHardshipReviewWithSolicitorCostsAndExtraExpenditure_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getMagsHardshipReviewWithDetails(SOL_COSTS, EXPENDITURE);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(-13945.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.PASS);
    }

    @Test
    void givenHardshipReviewWithAllDetailTypes_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getMagsHardshipReviewWithDetails(SOL_COSTS, EXPENDITURE, INCOME);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(-15945.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.PASS);
    }

    @Test
    void givenHardshipReviewWithZeroAmount_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getCrownHardshipReviewWithDetails(EXPENDITURE);

        hardship.getExtraExpenditure().get(0)
                .setAmount(BigDecimal.ZERO);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(5000.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.FAIL);
    }

    @Test
    void givenHardshipReviewWithRejectedExtraExpenditure_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getCrownHardshipReviewWithDetails(EXPENDITURE);

        hardship.getExtraExpenditure().get(0)
                .setAccepted(false);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(5000.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.FAIL);
    }

    @Test
    void givenHardshipReviewWithEmptyDetails_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getCrownHardshipReviewWithDetails();

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(5000.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.FAIL);
    }

    @Test
    void givenHardshipReviewWithOtherFundingSource_whenCalculateHardshipIsInvoked_thenHardshipResultIsReturned() {
        HardshipReview hardship = TestModelDataBuilder.getCrownHardshipReviewWithDetails(FUNDING);

        HardshipResult response =
                hardshipService.calculateHardship(hardship, FULL_THRESHOLD);

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(BigDecimal.valueOf(5000.00).setScale(2, RoundingMode.HALF_UP));

        softly.assertThat(response.getResult())
                .isEqualTo(HardshipReviewResult.FAIL);
    }
}
