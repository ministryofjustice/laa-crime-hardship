package uk.gov.justice.laa.crime.hardship.validation;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.exeption.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetailReason;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewProgress;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewProgressResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HardshipReviewValidatorTest {

    public static final HardshipReviewDetailCode detailCode = HardshipReviewDetailCode.ADD_MORTGAGE;
    public static final String REASON_NOTE = "Reason Note";
    public static final String NEW_WORK_REASON_CODE = "Reason Code";

    @Test
    void givenValidRequest_whenValidateHardshipReviewProgressItemIsInvoked_thenNoExceptionIsRaised() {
        HardshipReviewProgress hardshipReviewProgress = TestModelDataBuilder.buildHardshipReviewProgress();
        assertThat(HardshipReviewValidator.validateHardshipReviewProgressItem(hardshipReviewProgress)).isEmpty();
    }

    @Test
    void givenValidRequest_whenValidateHardshipReviewExpenditureItemIsInvoked_thenNoExceptionIsRaised() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildValidHardshipReviewDetail();
        assertThat(HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenValidRequest_whenValidateHardshipReviewFundingItemIsInvoked_thenNoExceptionIsRaised() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildValidHardshipReviewDetail();
        assertThat(HardshipReviewValidator.validateHardshipReviewFundingItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenValidRequest_whenValidateHardshipMandatoryFieldsIsInvoked_thenNoExceptionIsRaised() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        assertThat(HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO)).isEmpty();
    }

    @Test
    void givenValidRequest_whenValidateCompletedHardshipIsInvoked_thenNoExceptionIsRaised() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        assertThat(HardshipReviewValidator.validateCompletedHardship(hardshipReviewDTO)).isEmpty();
    }

    @Test
    void givenHardshipReviewProgessWithNullDateRequested_whenValidateIsInvoked_thenValidationFails() {
        HardshipReviewProgress hardshipReviewProgress = TestModelDataBuilder.buildHardshipReviewProgress(null,
                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED, LocalDateTime.now());

        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewProgressItem(hardshipReviewProgress))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DATE);
    }

    @Test
    void givenHardshipReviewProgessWithNullProgressResponse_whenValidateIsInvoked_thenValidationFails() {
        HardshipReviewProgress hardshipReviewProgress = TestModelDataBuilder.buildHardshipReviewProgress(LocalDateTime.now(),
                null, LocalDateTime.now());

        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewProgressItem(hardshipReviewProgress))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DATE);
    }

    @Test
    void givenHardshipReviewProgessWithNullDateRequired_whenValidateReviewProgressItemIsInvoked_thenValidationFails() {
        HardshipReviewProgress hardshipReviewProgress = TestModelDataBuilder.buildHardshipReviewProgress(LocalDateTime.now(),
                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED, null);

        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewProgressItem(hardshipReviewProgress))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DATE);
    }


    @Test
    void givenHardshipReviewDetailWithNullDescription_whenValidateReviewExpenditureIsInvoked_thenEmptyIsReturned() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(null, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThat(HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenAValidDescriptionAndNullValues_whenValidateReviewExpenditureIsInvoked_thenEmptyIsReturned() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(null, null,
                null, null, REASON_NOTE, LocalDateTime.now());
        hardshipReviewDetail.setDetailCode(null);
        assertThat(HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenHardshipReviewDetailWithNullDetailCodeAndNullAmount_whenValidateReviewExpenditureIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(null, null,
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThat(HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenHardshipReviewDetailWithNullFrequency_whenValidateReviewExpenditureIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                null, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailWithNullDetailReason_whenValidateReviewExpenditureIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, null, REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailWithNullDetailReasonId_whenValidateReviewExpenditureIsInvoked_thenValidationFails() {
        HardshipReviewDetailReason hardshipReviewDetailReason = TestModelDataBuilder.buildHardshipReviewDetailReason();
        hardshipReviewDetailReason.setId(null);
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, hardshipReviewDetailReason, REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailWithDescriptionAndNullAmount_whenValidateReviewExpenditureIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, null,
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailWithDescriptionAndNullDetailReason_whenValidateReviewExpenditureIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, null, REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewExpenditureItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }


    @Test
    void givenValidRequest_whenValidateHardshipReviewIncomeItemIsInvoked_thenNoExceptionIsRaised() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildValidHardshipReviewDetail();
        assertThat(HardshipReviewValidator.validateHardshipReviewIncomeItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenHardshipReviewDetailWithNullDetailCode_whenValidateReviewIncomeIsInvoked_thenReturnEmpty() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(null, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThat(HardshipReviewValidator.validateHardshipReviewIncomeItem(hardshipReviewDetail)).isEmpty();
    }

    @Test
    void givenHardshipReviewDetailWithDescriptionAndNullAmount_whenValidateReviewIncomeIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, null,
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), null, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewIncomeItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailWithDescriptionAndNullFrequency_whenValidateReviewIncomeIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                null, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewIncomeItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailWithDescriptionAndNullReasonNote_whenValidateReviewIncomeIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), null, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewIncomeItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }


    @Test
    void givenHardshipReviewDetailAndNullAmount_whenValidateReviewFundingItemIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, null,
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, LocalDateTime.now());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewFundingItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }

    @Test
    void givenHardshipReviewDetailAndNullDateDue_whenValidateReviewFundingItemIsInvoked_thenValidationFails() {
        HardshipReviewDetail hardshipReviewDetail = TestModelDataBuilder.buildHardshipReviewDetail(detailCode, BigDecimal.valueOf(10),
                Frequency.ANNUALLY, TestModelDataBuilder.buildHardshipReviewDetailReason(), REASON_NOTE, null);
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipReviewFundingItem(hardshipReviewDetail))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_DETAIL_IN_SECTION + hardshipReviewDetail.getDescription());
    }


    @Test
    void givenHardshipReviewDTOAndNullNewWorkReasonCode_whenValidateHardshipMandatoryFieldsIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(null, LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_REVIEW_REASON);
    }

    @Test
    void givenHardshipReviewDTOWithNullSolicitorCosts_whenValidateHardshipMandatoryFieldsIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, LocalDateTime.now(),
                null);
        assertThat(HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO)).isEmpty();

    }

    @Test
    void givenHardshipReviewDTOWithNullSolicitorRate_whenValidateHardshipMandatoryFieldsIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(null).solicitorHours(BigDecimal.valueOf(100)).build());
        assertThat(HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO)).isEmpty();
    }

    @Test
    void givenHardshipReviewDTOWithSolictorRateAndNullSolicitorHours_whenValidateHardshipMandatoryFieldsIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(null).build());
        assertThatThrownBy(() -> HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_FIELD);
    }


    @Test
    void givenHardshipReviewDTOWithNullReviewStatus_whenValidateCompletedHardshipIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.setReviewStatus(null);
        assertThat(HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO)).isEmpty();
    }

    @Test
    void givenHardshipReviewDTOWithInProgresReviewStatus_whenValidateCompletedHardshipIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.setReviewStatus(HardshipReviewStatus.IN_PROGRESS);
        assertThat(HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO)).isEmpty();
    }

    @Test
    void givenHardshipReviewDTOWithInProgressReviewStatusAndNullReviewDate_whenValidateCompletedHardshipIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, null,
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.setReviewStatus(HardshipReviewStatus.IN_PROGRESS);
        assertThat(HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO)).isEmpty();
    }

    @Test
    void givenHardshipReviewDTOWithNullReviewDate_whenValidateCompletedHardshipIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, null,
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        assertThatThrownBy(() -> HardshipReviewValidator.validateCompletedHardship(hardshipReviewDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_REVIEW_DATE);
    }

    @Test
    void givenNullReviewStatusAndReviewDate_whenValidateCompletedHardshipIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, null,
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.setReviewStatus(null);
        assertThat(HardshipReviewValidator.validateCompletedHardship(hardshipReviewDTO)).isEmpty();

    }

    @Test
    void givenHardshipReviewDTOWithCompleteAndNullReviewDate_whenValidateCompletedHardshipIsInvoked_thenValidationFails() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, null,
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.setReviewStatus(HardshipReviewStatus.COMPLETE);
        assertThatThrownBy(() -> HardshipReviewValidator.validateCompletedHardship(hardshipReviewDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipReviewValidator.MSG_INVALID_REVIEW_DATE);
    }

}
