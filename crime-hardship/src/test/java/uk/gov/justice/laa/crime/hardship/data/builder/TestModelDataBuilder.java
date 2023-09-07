package uk.gov.justice.laa.crime.hardship.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDetail;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    public static final Integer CMU_ID = 50;
    public static final Integer HARDSHIP_ID = 1234;
    public static final Integer TEST_REP_ID = 91919;
    public static final String TEST_USER_NAME = "mock-u";
    public static final String DETAIL_TYPE = "EXPENDITURE";
    public static final Integer FINANCIAL_ASSESSMENT_ID = 6781;
    public static final BigDecimal FULL_THRESHOLD = BigDecimal.valueOf(3000.0);
    public static final BigDecimal HARDSHIP_AMOUNT = BigDecimal.valueOf(5000);
    public static final BigDecimal TOTAL_DISPOSABLE_INCOME = BigDecimal.valueOf(500);
    public static final BigDecimal POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(250);
    public static final String MEANS_ASSESSMENT_TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    public static final LocalDateTime RESULT_DATE = LocalDateTime.of(2022, 12, 14, 0, 0, 0);

    // Solicitors Costs
    public static final Integer TEST_SOLICITOR_HOURS = 50;
    public static final BigDecimal TEST_SOLICITOR_RATE = BigDecimal.valueOf(200);
    public static final BigDecimal TEST_SOLICITOR_DISBURSEMENTS = BigDecimal.valueOf(375);
    public static final BigDecimal TEST_SOLICITOR_VAT = BigDecimal.valueOf(250);
    public static final BigDecimal TEST_SOLICITOR_ESTIMATED_COST = BigDecimal.valueOf(2500);

    public static ApiCalculateHardshipByDetailRequest getApiCalculateHardshipByDetailRequest(boolean isValid) {
        return new ApiCalculateHardshipByDetailRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withLaaTransactionId(MEANS_ASSESSMENT_TRANSACTION_ID)
                .withDetailType(DETAIL_TYPE);
    }

    public static HardshipReview getMinimalHardshipReview() {
        return new HardshipReview()
                .withCourtType(CourtType.MAGISTRATE)
                .withReviewDate(LocalDateTime.now())
                .withSolicitorCosts(TestModelDataBuilder.getSolicitorsCosts())
                .withTotalAnnualDisposableIncome(TOTAL_DISPOSABLE_INCOME);
    }

    public static HardshipMetadata getHardshipMetadata() {
        return new HardshipMetadata()
                .withReviewReason(NewWorkReason.PRI)
                .withCmuId(CMU_ID)
                .withHardshipReviewId(HARDSHIP_ID)
                .withNotes("Mock Note.")
                .withDecisionNotes("Mock Decision Note.")
                .withRepId(TestModelDataBuilder.TEST_REP_ID)
                .withReviewStatus(HardshipReviewStatus.COMPLETE)
                .withUserSession(TestModelDataBuilder.getUserSession())
                .withFinancialAssessmentId(TestModelDataBuilder.FINANCIAL_ASSESSMENT_ID);
    }

    public static HardshipResult getHardshipResult(HardshipReviewResult result) {
        return HardshipResult.builder()
                .resultDate(RESULT_DATE)
                .result(result)
                .postHardshipDisposableIncome(POST_HARDSHIP_DISPOSABLE_INCOME)
                .build();
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(TEST_SOLICITOR_VAT)
                .withDisbursements(BigDecimal.ZERO)
                .withRate(TEST_SOLICITOR_RATE)
                .withHours(TEST_SOLICITOR_HOURS)
                .withEstimatedTotal(TEST_SOLICITOR_ESTIMATED_COST);
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

    public static HardshipReview getMagsHardshipReviewWithDetails(HardshipReviewDetailType... detailTypes) {
        return getHardshipReviewWithDetails(CourtType.MAGISTRATE, detailTypes);
    }

    public static HardshipReview getCrownHardshipReviewWithDetails(HardshipReviewDetailType... detailTypes) {
        return getHardshipReviewWithDetails(CourtType.CROWN_COURT, detailTypes);
    }

    private static HardshipReview getHardshipReviewWithDetails(CourtType courtType,
                                                               HardshipReviewDetailType... detailTypes) {

        HardshipReview hardship = new HardshipReview()
                .withCourtType(courtType)
                .withTotalAnnualDisposableIncome(HARDSHIP_AMOUNT);

        Stream.of(detailTypes)
                .forEach(type -> {
                    switch (type) {
                        case EXPENDITURE -> hardship.setExtraExpenditure(
                                List.of(
                                        new ExtraExpenditure()
                                                .withAccepted(true)
                                                .withAmount(BigDecimal.valueOf(160.00))
                                                .withFrequency(Frequency.WEEKLY)
                                )
                        );
                        case SOL_COSTS -> hardship.setSolicitorCosts(
                                new SolicitorCosts()
                                        .withRate(TEST_SOLICITOR_RATE)
                                        .withDisbursements(TEST_SOLICITOR_DISBURSEMENTS)
                                        .withVat(TEST_SOLICITOR_VAT)
                                        .withEstimatedTotal(TEST_SOLICITOR_ESTIMATED_COST)
                                        .withHours(TEST_SOLICITOR_HOURS)
                        );
                        case INCOME -> hardship.setDeniedIncome(
                                List.of(
                                        new DeniedIncome()
                                                .withAccepted(true)
                                                .withAmount(BigDecimal.valueOf(2000.00))
                                                .withFrequency(Frequency.ANNUALLY)
                                )
                        );
                        case FUNDING -> hardship.setOtherFundingSources(
                                List.of(
                                        new OtherFundingSource()
                                                .withAmount(BigDecimal.valueOf(1000.00))
                                                .withDescription("Support from parents")
                                                .withDueDate(LocalDateTime.now())
                                )
                        );
                    }
                });

        return hardship;
    }
}
