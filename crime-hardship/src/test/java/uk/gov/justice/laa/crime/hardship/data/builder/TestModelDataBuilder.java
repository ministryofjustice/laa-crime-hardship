package uk.gov.justice.laa.crime.hardship.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDTO;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDetail;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
public class TestModelDataBuilder {

    public static final Integer CMU_ID = 50;
    public static final Integer HARDSHIP_ID = 1234;
    public static final Integer TEST_REP_ID = 91919;
    public static final String TEST_USER_NAME = "mock-u";
    public static final String DETAIL_TYPE = "EXPENDITURE";
    public static final Integer FINANCIAL_ASSESSMENT_ID = 6781;
    public static final BigDecimal FULL_THRESHOLD = BigDecimal.valueOf(3000.0);
    public static final BigDecimal HARDSHIP_AMOUNT = BigDecimal.valueOf(10.0);
    public static final BigDecimal HARDSHIP_SUMMARY = BigDecimal.valueOf(100.12);
    public static final BigDecimal TOTAL_DISPOSABLE_INCOME = BigDecimal.valueOf(500);
    public static final BigDecimal POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(250);
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final LocalDateTime RESULT_DATE = LocalDateTime.of(2022, 12, 14, 0, 0, 0);

    public static ApiCalculateHardshipByDetailRequest getApiCalculateHardshipByDetailRequest(boolean isValid) {
        return new ApiCalculateHardshipByDetailRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withDetailType(DETAIL_TYPE);
    }

    public static ApiCalculateHardshipByDetailResponse getApiCalculateHardshipByDetailResponse() {
        return new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(HARDSHIP_SUMMARY);
    }

    public static HardshipReview getMinimalHardshipReview() {
        return new HardshipReview()
                .withReviewReason(NewWorkReason.PRI)
                .withCmuId(CMU_ID)
                .withReviewDate(LocalDateTime.now())
                .withHardshipReviewId(HARDSHIP_ID)
                .withNotes("Mock Note.")
                .withDecisionNotes("Mock Decision Note.")
                .withSolicitorCosts(TestModelDataBuilder.getSolicitorsCosts())
                .withTotalAnnualDisposableIncome(TOTAL_DISPOSABLE_INCOME)
                .withReviewStatus(HardshipReviewStatus.COMPLETE)

                .withRepId(TestModelDataBuilder.TEST_REP_ID)
                .withUserSession(TestModelDataBuilder.getUserSession())
                .withCourtType(CourtType.MAGISTRATE)
                .withFinancialAssessmentId(TestModelDataBuilder.FINANCIAL_ASSESSMENT_ID);
    }

    public static HardshipResult getHardshipResult() {
        return HardshipResult.builder()
                .resultDate(RESULT_DATE)
                .result(HardshipReviewResult.PASS)
                .postHardshipDisposableIncome(POST_HARDSHIP_DISPOSABLE_INCOME)
                .build();
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(BigDecimal.TEN)
                .withDisbursements(BigDecimal.ZERO)
                .withRate(BigDecimal.TEN)
                .withHours(50)
                .withEstimatedTotal(BigDecimal.valueOf(2000));
    }

    public static DeniedIncome getDeniedIncome() {
        return new DeniedIncome()
                .withAccepted(true)
                .withAmount(BigDecimal.TEN)
                .withFrequency(Frequency.MONTHLY)
                .withReasonNote("Hospitalisation")
                .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS);
    }

    public static ExtraExpenditure getExtraExpenditure() {
        return new ExtraExpenditure()
                .withAccepted(true)
                .withAmount(BigDecimal.TEN)
                .withFrequency(Frequency.TWO_WEEKLY)
                .withReasonCode(HardshipReviewDetailReasons.ESSENTIAL_ITEM)
                .withItemCode(ExtraExpenditureDetailCode.CARDS);
    }

    public static OtherFundingSource getOtherFundingSources() {
        return new OtherFundingSource()
                .withAmount(BigDecimal.ONE)
                .withDueDate(LocalDateTime.MAX)
                .withDescription("Loan from parents");
    }

    public static HardshipProgress getHardshipProgress() {
        return new HardshipProgress()
                .withDateTaken(LocalDateTime.of(
                        LocalDate.ofYearDay(2022, 235), LocalTime.NOON))
                .withDateCompleted(LocalDateTime.of(
                        LocalDate.ofYearDay(2022, 250), LocalTime.NOON)
                )
                .withDateRequired(LocalDateTime.of(
                        LocalDate.ofYearDay(2022, 300), LocalTime.NOON)
                )
                .withAction(HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                .withResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED);
    }

    public static ApiUserSession getUserSession() {
        return new ApiUserSession()
                .withUserName(TEST_USER_NAME)
                .withSessionId(UUID.randomUUID().toString());
    }

    public static List<HardshipReviewDetail> getHardshipReviewDetailList(String accepted, double amount) {
        return List.of(HardshipReviewDetail.builder()
                .id(HARDSHIP_ID)
                .accepted(accepted)
                .amount(BigDecimal.valueOf(amount))
                .frequency(Frequency.ANNUALLY)
                .build());
    }

    public static HardshipReviewCalculationDTO getHardshipReviewCalculationDTO(HardshipReviewDetailType... hardshipReviewDetailTypes) {
        var hardshipReviewCalculationDetails = Arrays.stream(hardshipReviewDetailTypes)
                .map(TestModelDataBuilder::getHardshipReviewCalculationDetail)
                .collect(toList());

        return HardshipReviewCalculationDTO.builder()
                .hardshipReviewCalculationDetails(hardshipReviewCalculationDetails)
                .disposableIncome(BigDecimal.valueOf(5000.00)).build();
    }

    public static HardshipReviewCalculationDetail getHardshipReviewCalculationDetail(HardshipReviewDetailType detailType) {
        switch (detailType) {
            case EXPENDITURE -> {
                return HardshipReviewCalculationDetail.builder()
                        .detailType(HardshipReviewDetailType.EXPENDITURE)
                        .accepted("Y")
                        .amount(BigDecimal.valueOf(160.00))
                        .frequency(Frequency.WEEKLY)
                        .build();
            }
            case SOL_COSTS -> {
                return HardshipReviewCalculationDetail.builder()
                        .detailType(HardshipReviewDetailType.SOL_COSTS)
                        .accepted("Y")
                        .amount(BigDecimal.valueOf(2300.25))
                        .frequency(Frequency.ANNUALLY)
                        .build();
            }
            case INCOME -> {
                return HardshipReviewCalculationDetail.builder()
                        .detailType(HardshipReviewDetailType.INCOME)
                        .accepted("Y")
                        .amount(BigDecimal.valueOf(2000.00))
                        .frequency(Frequency.ANNUALLY)
                        .build();
            }
            default -> {
                return HardshipReviewCalculationDetail.builder()
                        .detailType(detailType)
                        .build();
            }
        }
    }

}
