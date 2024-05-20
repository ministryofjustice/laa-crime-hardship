package uk.gov.justice.laa.crime.hardship.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.common.model.hardship.DeniedIncome;
import uk.gov.justice.laa.crime.common.model.hardship.ExtraExpenditure;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.common.model.hardship.SolicitorCosts;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.enums.DeniedIncomeDetailCode;
import uk.gov.justice.laa.crime.enums.ExtraExpenditureDetailCode;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static uk.gov.justice.laa.crime.enums.HardshipReviewDetailType.*;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipDetailMapperTest {

    HardshipDetailMapper mapper = new HardshipDetailMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenApiHardshipDetailListWithExpenditure_whenToDtoIsInvoked_thenDtoIsMapped() {

        List<ApiHardshipDetail> hardshipDetails =
                TestModelDataBuilder.getApiHardshipReviewDetails(EXPENDITURE);

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
                TestModelDataBuilder.getApiHardshipReviewDetails(INCOME);

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
                TestModelDataBuilder.getApiHardshipReviewDetails(SOL_COSTS);

        ApiHardshipDetail detail = hardshipDetails.get(0);

        HardshipReview hardship = new HardshipReview();
        mapper.toDto(hardshipDetails, hardship);

        SolicitorCosts solicitorCosts = hardship.getSolicitorCosts();
        softly.assertThat(solicitorCosts.getEstimatedTotal())
                .isEqualTo(detail.getAmount());
    }

    @Test
    void givenApiHardshipDetailListInvalidDetailType_whenToDtoIsInvoked_thenExceptionIsThrown() {
        ApiHardshipDetail detail = new ApiHardshipDetail()
                .withDetailType(ACTION);

        assertThatThrownBy(
                () -> mapper.toDto(List.of(detail), new HardshipReview())
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unexpected value: ACTION");
    }
}
