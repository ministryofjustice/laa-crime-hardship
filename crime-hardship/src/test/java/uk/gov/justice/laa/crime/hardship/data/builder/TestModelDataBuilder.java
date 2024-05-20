package uk.gov.justice.laa.crime.hardship.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.hardship.*;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiHardshipDetail;
import uk.gov.justice.laa.crime.common.model.hardship.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    public static final Integer CMU_ID = 50;
    public static final Integer HARDSHIP_ID = 1234;
    public static final LocalDateTime ASSESSMENT_DATE = LocalDateTime.of(2022, 12, 14, 0, 0, 0);
    public static final Integer TEST_REP_ID = 91919;

    public static final String TEST_USER_NAME = "mock-u";
    public static final String DETAIL_TYPE = "EXPENDITURE";
    public static final Integer FINANCIAL_ASSESSMENT_ID = 6781;
    public static final BigDecimal FULL_THRESHOLD = BigDecimal.valueOf(3000.0);
    public static final BigDecimal HARDSHIP_AMOUNT = BigDecimal.valueOf(5000);
    public static final BigDecimal TOTAL_DISPOSABLE_INCOME = BigDecimal.valueOf(500);
    public static final BigDecimal POST_HARDSHIP_DISPOSABLE_INCOME = BigDecimal.valueOf(250);
    public static final LocalDate RESULT_DATE = LocalDate.of(2022, 12, 14);

    // Solicitors Costs
    public static final BigDecimal TEST_SOLICITOR_HOURS = BigDecimal.valueOf(50);
    public static final BigDecimal TEST_SOLICITOR_RATE = BigDecimal.valueOf(200);
    public static final BigDecimal TEST_SOLICITOR_DISBURSEMENTS = BigDecimal.valueOf(375);
    public static final BigDecimal TEST_SOLICITOR_VAT = BigDecimal.valueOf(250);
    public static final BigDecimal TEST_SOLICITOR_ESTIMATED_COST = BigDecimal.valueOf(2500);

    public static ApiCalculateHardshipRequest getApiCalculateHardshipRequest() {
        return new ApiCalculateHardshipRequest()
                .withHardship(getHardshipReview());
    }

    public static ApiCalculateHardshipResponse getApiCalculateHardshipResponse() {
        return new ApiCalculateHardshipResponse()
                .withReviewResult(getHardshipResult(HardshipReviewResult.PASS).getResult())
                .withPostHardshipDisposableIncome(BigDecimal.TEN);
    }

    public static ApiCalculateHardshipByDetailRequest getApiCalculateHardshipByDetailRequest(
            boolean isValid, HardshipReviewDetailType detailType) {

        return new ApiCalculateHardshipByDetailRequest()
                .withRepId(isValid ? TEST_REP_ID : null)
                .withDetailType(detailType.getType());
    }

    public static ApiPerformHardshipRequest getApiPerformHardshipRequest() {
        return new ApiPerformHardshipRequest()
                .withHardship(getHardshipReview())
                .withHardshipMetadata(getHardshipMetadata());
    }

    public static ApiPersistHardshipResponse getApiPersistHardshipResponse() {
        return new ApiPersistHardshipResponse()
                .withId(1000)
                .withDateCreated(LocalDateTime.now());
    }

    public static ApiPerformHardshipResponse getApiPerformHardshipResponse() {
        return new ApiPerformHardshipResponse()
                .withHardshipReviewId(1000)
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(BigDecimal.valueOf(3500))
                .withPostHardshipDisposableIncome(BigDecimal.TEN);
    }

    public static ApiCalculateHardshipByDetailResponse getApiCalculateHardshipByDetailResponse() {
        return new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(BigDecimal.valueOf(3500));
    }

    public static ApiFindHardshipResponse getApiFindHardshipResponse() {
        return new ApiFindHardshipResponse()
                .withId(HARDSHIP_ID)
                .withCmuId(999)
                .withNotes("Test note.")
                .withDecisionNotes("Test decision note.")
                .withReviewDate(LocalDateTime.now())
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(BigDecimal.valueOf(999.99))
                .withDisposableIncomeAfterHardship(BigDecimal.valueOf(99.99))
                .withNewWorkReason(NewWorkReason.PRI)
                .withSolicitorCosts(TestModelDataBuilder.getSolicitorsCosts())
                .withStatus(HardshipReviewStatus.COMPLETE)
                .withReviewDetails(getApiHardshipReviewDetails(BigDecimal.valueOf(99.99), HardshipReviewDetailType.EXPENDITURE))
                .withReviewProgressItems(null);
    }

    public static HardshipReview getMinimalHardshipReview() {
        return new HardshipReview()
                .withCourtType(CourtType.MAGISTRATE)
                .withReviewDate(LocalDateTime.now())
                .withSolicitorCosts(TestModelDataBuilder.getSolicitorsCosts())
                .withTotalAnnualDisposableIncome(TOTAL_DISPOSABLE_INCOME);
    }

    public static HardshipReview getHardshipReview() {
        return new HardshipReview()
                .withCourtType(CourtType.MAGISTRATE)
                .withReviewDate(LocalDateTime.now())
                .withSolicitorCosts(TestModelDataBuilder.getSolicitorsCosts())
                .withTotalAnnualDisposableIncome(TOTAL_DISPOSABLE_INCOME)
                .withSolicitorCosts(getSolicitorsCosts())
                .withDeniedIncome(List.of(getDeniedIncome()))
                .withExtraExpenditure(List.of(getExtraExpenditure().withDescription("Extra Expenditure")));
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
                .withReasonCode(HardshipReviewDetailReason.ESSENTIAL_ITEM)
                .withItemCode(ExtraExpenditureDetailCode.CARDS);
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

    public static HardshipReview getMagsHardshipReviewWithDetails(HardshipReviewDetailType... detailTypes) {
        return getHardshipReviewWithDetails(CourtType.MAGISTRATE, detailTypes);
    }

    public static HardshipReview getCrownHardshipReviewWithDetails(HardshipReviewDetailType... detailTypes) {
        return getHardshipReviewWithDetails(CourtType.CROWN_COURT, detailTypes);
    }

    public static List<ApiHardshipDetail> getApiHardshipReviewDetails(HardshipReviewDetailType... detailTypes) {
        return getApiHardshipReviewDetails(BigDecimal.TEN, detailTypes);
    }

    public static List<ApiHardshipDetail> getApiHardshipReviewDetails(BigDecimal amount,
                                                                      HardshipReviewDetailType... detailTypes) {
        List<ApiHardshipDetail> details = new ArrayList<>();

        Arrays.stream(detailTypes).forEach(type -> {
            switch (type) {
                case INCOME -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.INCOME)
                                .withAmount(amount)
                                .withFrequency(Frequency.MONTHLY)
                                .withAccepted("N")
                                .withOtherDescription("Statutory sick pay")
                                .withDetailCode(HardshipReviewDetailCode.SUSPENDED_WORK)
                );
                case EXPENDITURE -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                .withAmount(amount)
                                .withFrequency(Frequency.TWO_WEEKLY)
                                .withAccepted("Y")
                                .withDetailReason(HardshipReviewDetailReason.COVERED_BY_LIVING_EXPENSE)
                                .withOtherDescription("Loan to family members")
                                .withDetailCode(HardshipReviewDetailCode.OTHER)
                );
                case SOL_COSTS -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.SOL_COSTS)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("Y")
                );
            }
        });
        return details;
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
                    }
                });

        return hardship;
    }

    public static HardshipReviewDTO getHardshipReviewDTO() {
        return HardshipReviewDTO.builder()
                .hardship(TestModelDataBuilder.getHardshipReview())
                .hardshipMetadata(TestModelDataBuilder.getHardshipMetadata())
                .build();
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDTO() {
        return FinancialAssessmentDTO.builder()
                .id(FINANCIAL_ASSESSMENT_ID)
                .initialAssessmentDate(ASSESSMENT_DATE)
                .fullAssessmentDate(ASSESSMENT_DATE)
                .replaced("N")
                .dateCompleted(ASSESSMENT_DATE)
                .build();
    }

}
