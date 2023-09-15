package uk.gov.justice.laa.crime.hardship.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipDetailMapperTest {

    HardshipDetailMapper mapper = new HardshipDetailMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiHardshipDetailListWithExpenditure_whenToDtoIsInvoked_thenDtoIsMapped() {

        List<ApiHardshipDetail> hardshipDetails =
                TestModelDataBuilder.getApiHardshipReviewDetails(HardshipReviewDetailType.EXPENDITURE);

        ApiHardshipDetail detail = hardshipDetails.get(0);

        HardshipReview hardship = new HardshipReview();
        mapper.toDto(hardshipDetails, hardship);

        ExtraExpenditure expenditure = hardship.getExtraExpenditure().get(0);
        softly.assertThat(expenditure.getAccepted())
                .isTrue();
        softly.assertThat(expenditure.getAmount())
                .isEqualTo(detail.getAmount());
        softly.assertThat(expenditure.getFrequency())
                .isEqualTo(detail.getFrequency());
        softly.assertThat(expenditure.getReasonCode())
                .isEqualTo(detail.getDetailReason());
        softly.assertThat(expenditure.getDescription())
                .isEqualTo(detail.getOtherDescription());
        softly.assertThat(expenditure.getItemCode())
                .isEqualTo(ExtraExpenditureDetailCode.valueOf(detail.getDetailCode().getCode()));
    }

    @Test
    void givenApiHardshipDetailListWithDeniedIncome_whenToDtoIsInvoked_thenDtoIsMapped() {

        List<ApiHardshipDetail> hardshipDetails =
                TestModelDataBuilder.getApiHardshipReviewDetails(HardshipReviewDetailType.INCOME);

        ApiHardshipDetail detail = hardshipDetails.get(0);

        HardshipReview hardship = new HardshipReview();
        mapper.toDto(hardshipDetails, hardship);

        DeniedIncome income = hardship.getDeniedIncome().get(0);
        softly.assertThat(income.getAccepted())
                .isFalse();
        softly.assertThat(income.getAmount())
                .isEqualTo(detail.getAmount());
        softly.assertThat(income.getFrequency())
                .isEqualTo(detail.getFrequency());
        softly.assertThat(income.getDescription())
                .isEqualTo(detail.getOtherDescription());
        softly.assertThat(income.getItemCode())
                .isEqualTo(DeniedIncomeDetailCode.getFrom(detail.getDetailCode().getCode()));
    }

    @Test
    void givenApiHardshipDetailListWithSolicitorCosts_whenToDtoIsInvoked_thenDtoIsMapped() {

        List<ApiHardshipDetail> hardshipDetails =
                TestModelDataBuilder.getApiHardshipReviewDetails(HardshipReviewDetailType.SOL_COSTS);

        ApiHardshipDetail detail = hardshipDetails.get(0);

        HardshipReview hardship = new HardshipReview();
        mapper.toDto(hardshipDetails, hardship);

        SolicitorCosts solicitorCosts = hardship.getSolicitorCosts();
        softly.assertThat(solicitorCosts.getEstimatedTotal())
                .isEqualTo(detail.getAmount());
    }

    @Test
    void givenApiHardshipDetailListWithOtherFunding_whenToDtoIsInvoked_thenDtoIsMapped() {

        List<ApiHardshipDetail> hardshipDetails =
                TestModelDataBuilder.getApiHardshipReviewDetails(HardshipReviewDetailType.FUNDING);

        ApiHardshipDetail detail = hardshipDetails.get(0);

        HardshipReview hardship = new HardshipReview();
        mapper.toDto(hardshipDetails, hardship);

        OtherFundingSource otherFunding = hardship.getOtherFundingSources().get(0);
        softly.assertThat(otherFunding.getAmount())
                .isEqualTo(detail.getAmount());
        softly.assertThat(otherFunding.getDueDate())
                .isEqualTo(detail.getDateDue());
        softly.assertThat(otherFunding.getDescription())
                .isEqualTo(detail.getOtherDescription());
    }

    @Test
    void givenApiHardshipDetailListInvalidDetailType_whenToDtoIsInvoked_thenExceptionIsThrown() {
        ApiHardshipDetail detail = new ApiHardshipDetail()
                .withType(HardshipReviewDetailType.ACTION);

        assertThatThrownBy(
                () -> mapper.toDto(List.of(detail), new HardshipReview())
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unexpected value: ACTION");
    }
}
