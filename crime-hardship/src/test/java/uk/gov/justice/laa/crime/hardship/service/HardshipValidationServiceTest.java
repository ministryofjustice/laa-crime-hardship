package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.HardshipMetadata;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HardshipValidationServiceTest {

    public HardshipValidationService hardshipValidationService;

    @BeforeEach
    void setUp() {
        hardshipValidationService = new HardshipValidationService();
    }

    @ParameterizedTest
    @MethodSource("apiPerformHardshipRequestDataForNoValidationException")
    void hardshipValidationServiceWithNoValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(() -> hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    @ParameterizedTest
    @MethodSource("hardshipReviewStatusDataForValidationException")
    void validateHardshipReviewStatus_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException = assertThrows(ValidationException.class, ()->hardshipValidationService.checkHardship(apiPerformHardshipRequest));
        assertEquals("Review Date must be entered for completed hardship", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("hardshipReviewStatusDataForNoValidationException")
    void validateHardshipReviewStatus_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(()-> hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    @ParameterizedTest
    @MethodSource("newWorkReasonDataForValidationException")
    void validateHardshipReviewNewWorkReason(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest));
        assertEquals("Review Reason must be entered for hardship", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("solicitorDataForNoValidationException")
    void validateSolicitorDetails_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(()-> hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    @ParameterizedTest
    @MethodSource("solicitorDataForValidationException")
    void validateSolicitorDetails_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(apiPerformHardshipRequest));
        assertEquals("Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("deniedIncomeDataForValidationException")
    void validateDeniedIncome_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(apiPerformHardshipRequest));
        assertEquals("Amount, Frequency, and Reason must be entered for each detail in section Medical Grounds", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("deniedIncomeDataForNoValidationException")
    void validateDeniedIncome_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(()->hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    @ParameterizedTest
    @MethodSource("expenditureDataForValidationException")
    void validateExtraExpenditure_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(apiPerformHardshipRequest));
        assertEquals("Amount, Frequency, and Reason must be entered for each detail in section Mortgage on additional Property", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("expenditureDataForNoValidationException")
    void validateExtraExpenditure_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(()->hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    @ParameterizedTest
    @MethodSource("progressionItemDataForValidationException")
    void validateProgressionItem_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(apiPerformHardshipRequest));
        assertEquals("Date Taken, Response Required, and Date Required must be entered for each Action Taken in section Review Progress", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("progressionItemDataForNoValidationException")
    void validateProgressionItem_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(()->hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    private static Stream<Arguments> apiPerformHardshipRequestDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(TestModelDataBuilder.getHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata())));
    }

    private static Stream<Arguments> hardshipReviewStatusDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().withReviewDate(null),
                        new HardshipMetadata().withReviewStatus(HardshipReviewStatus.COMPLETE))
                ));
    }

    private static Stream<Arguments> hardshipReviewStatusDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(TestModelDataBuilder.getMinimalHardshipReview().withReviewDate(LocalDateTime.now()),
                        TestModelDataBuilder.getHardshipMetadata().withReviewStatus(HardshipReviewStatus.COMPLETE))),
                Arguments.of(new ApiPerformHardshipRequest(TestModelDataBuilder.getMinimalHardshipReview().withReviewDate(LocalDateTime.now()),
                        TestModelDataBuilder.getHardshipMetadata().withReviewStatus(HardshipReviewStatus.IN_PROGRESS))));
    }

    private static Stream<Arguments> newWorkReasonDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview(),
                        new HardshipMetadata().withReviewReason(null))));
    }

    private static Stream<Arguments> solicitorDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ZERO).withHours(0)),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW))),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(new SolicitorCosts().withRate(null).withHours(0)),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW))),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(null),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW)))
        );
    }

    private static Stream<Arguments> solicitorDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(0)),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW))),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW))));
    }

    private static Stream<Arguments> deniedIncomeDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(List.of(TestModelDataBuilder.getDeniedIncome()
                                        .withItemCode(null)
                                        .withFrequency(null)
                                        .withAmount(null)
                                        .withReasonNote(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(null),
                        TestModelDataBuilder.getHardshipMetadata()))
        );
    }

    private static Stream<Arguments> deniedIncomeDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(List.of(TestModelDataBuilder.getDeniedIncome()
                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                        .withFrequency(null)
                                        .withAmount(BigDecimal.ONE)
                                        .withReasonNote("test"))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(List.of(TestModelDataBuilder.getDeniedIncome()
                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                        .withFrequency(Frequency.ANNUALLY)
                                        .withAmount(BigDecimal.ONE)
                                        .withReasonNote(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(List.of(TestModelDataBuilder.getDeniedIncome()
                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                        .withFrequency(Frequency.ANNUALLY)
                                        .withAmount(null)
                                        .withReasonNote("test"))),
                        TestModelDataBuilder.getHardshipMetadata()))
        );
    }

    private static Stream<Arguments> expenditureDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                        .withItemCode(ExtraExpenditureDetailCode.ADD_MORTGAGE)
                                        .withFrequency(null)
                                        .withAmount(BigDecimal.ONE)
                                        .withReasonCode(HardshipReviewDetailReason.ARRANGEMENT_IN_PLACE))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                        .withItemCode(ExtraExpenditureDetailCode.ADD_MORTGAGE)
                                        .withFrequency(Frequency.ANNUALLY)
                                        .withAmount(BigDecimal.ONE)
                                        .withReasonCode(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                        .withItemCode(ExtraExpenditureDetailCode.ADD_MORTGAGE)
                                        .withFrequency(Frequency.ANNUALLY)
                                        .withAmount(null)
                                        .withReasonCode(HardshipReviewDetailReason.ARRANGEMENT_IN_PLACE))),
                        TestModelDataBuilder.getHardshipMetadata()))
        );
    }

    private static Stream<Arguments> expenditureDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                        .withItemCode(null)
                                        .withFrequency(null)
                                        .withAmount(null)
                                        .withReasonCode(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(null),
                        TestModelDataBuilder.getHardshipMetadata()))
        );
    }

    private static Stream<Arguments> progressionItemDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                        .withAction(HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                        .withDateRequired(null)
                                        .withResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                        .withDateTaken(LocalDateTime.now()))))),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                        .withAction(HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                        .withDateRequired(LocalDateTime.now())
                                        .withResponse(null)
                                        .withDateTaken(LocalDateTime.now()))))),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                        .withAction(HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                        .withDateRequired(LocalDateTime.now())
                                        .withResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                        .withDateTaken(null)))))
        );
    }

    private static Stream<Arguments> progressionItemDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                        .withAction(null)
                                        .withDateRequired(null)
                                        .withResponse(null)
                                        .withDateTaken(null))
                        )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata().withProgressItems(null)))),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                        .withAction(HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                        .withDateRequired(LocalDateTime.now())
                                        .withResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                        .withDateTaken(LocalDateTime.now())))))
        );
    }
}
