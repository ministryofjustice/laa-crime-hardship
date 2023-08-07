package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class HardshipServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private HardshipService hardshipService;

    @Test
    void givenHardshipReviewAmount_whenCalculateHardshipByDetailIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);
        List<HardshipReviewDetail> hardshipReviewDetailList = TestModelDataBuilder.getHardshipReviewDetailList("Y", 100);
        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);
        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void givenHardshipDetailWithZeroAmount_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);
        List<HardshipReviewDetail> hardshipReviewDetailList = TestModelDataBuilder.getHardshipReviewDetailList("Y", 0);
        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);
        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenHardshipDetailWithNotAccepted_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);
        List<HardshipReviewDetail> hardshipReviewDetailList = TestModelDataBuilder.getHardshipReviewDetailList("N", 10);
        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);
        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.ZERO);
    }

}