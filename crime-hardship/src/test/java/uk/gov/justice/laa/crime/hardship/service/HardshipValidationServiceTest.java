package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.HardshipMetadata;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.*;
import uk.gov.justice.laa.crime.hardship.validation.HardshipValidationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HardshipValidationServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private HardshipValidationService hardshipValidationService;

    public static final LocalDateTime TODAYS_DATE = LocalDateTime.now();
    public static final LocalDateTime YESTERDAYS_DATE = LocalDateTime.now().minusDays(1);

    private void configureStubs() {
        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(FinancialAssessmentDTO.builder()
                                    .replaced("N")
                                    .dateCompleted(YESTERDAYS_DATE)
                                    .initialAssessmentDate(YESTERDAYS_DATE)
                                    .build()
                );
    }


    @Test
    void givenNoPrecedingFinancialAssessment_whenCheckHardshipIsInvoked_thenExceptionIsThrown() {
        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(TODAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(null);

        assertThatThrownBy(() -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review can only be entered after a completed assessment");
    }

    @Test
    void givenFinancialAssessmentIsReplaced_whenCheckHardshipIsInvoked_thenExceptionIsThrown() {
        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(TODAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(FinancialAssessmentDTO.builder()
                                    .replaced("Y")
                                    .dateCompleted(TODAYS_DATE)
                                    .build()
                );

        assertThatThrownBy(() -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review can only be entered after a completed assessment");
    }

    @Test
    void givenFinancialAssessmentIsIncomplete_whenCheckHardshipIsInvoked_thenExceptionIsThrown() {
        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(TODAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(FinancialAssessmentDTO.builder()
                                    .replaced("N")
                                    .dateCompleted(null)
                                    .build()
                );

        assertThatThrownBy(() -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review can only be entered after a completed assessment");
    }

    @Test
    void givenReviewDateIsAfterInitialAssessmentDate_whenCheckHardshipIsInvoked_thenNoExceptionIsThrown() {

        configureStubs();

        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(TODAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @Test
    void givenReviewDateIsBeforeInitialAssessmentDate_whenCheckHardshipIsInvoked_thenExceptionIsRaised() {
        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(YESTERDAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(FinancialAssessmentDTO.builder()
                                    .replaced("N")
                                    .dateCompleted(TODAYS_DATE)
                                    .initialAssessmentDate(TODAYS_DATE)
                                    .build()
                );

        assertThatThrownBy(() -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review date precedes the initial or full assessment date(s)");
    }

    @Test
    void givenReviewDateIsAfterFullAssessmentDate_whenCheckHardshipIsInvoked_thenNoExceptionIsThrown() {
        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(TODAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(FinancialAssessmentDTO.builder()
                                    .replaced("N")
                                    .dateCompleted(YESTERDAYS_DATE)
                                    .fullAssessmentDate(YESTERDAYS_DATE)
                                    .build()
                );

        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @Test
    void givenReviewDateIsBeforeFullAssessmentDate_whenCheckHardshipIsInvoked_thenExceptionIsRaised() {

        ApiPerformHardshipRequest apiPerformHardshipRequest =
                new ApiPerformHardshipRequest(
                        new HardshipReview()
                                .withReviewDate(YESTERDAYS_DATE),
                        TestModelDataBuilder.getHardshipMetadata()
                );

        when(maatCourtDataService.getFinancialAssessment(anyInt()))
                .thenReturn(FinancialAssessmentDTO.builder()
                                    .replaced("N")
                                    .dateCompleted(TODAYS_DATE)
                                    .initialAssessmentDate(YESTERDAYS_DATE)
                                    .fullAssessmentDate(TODAYS_DATE)
                                    .build()
                );

        assertThatThrownBy(() -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review date precedes the initial or full assessment date(s)");
    }

    @ParameterizedTest
    @MethodSource("apiPerformHardshipRequestDataForNoValidationException")
    void hardshipValidationServiceWithNoValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        configureStubs();
        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @ParameterizedTest
    @MethodSource("hardshipReviewStatusDataForValidationException")
    void validateHardshipReviewStatus_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertThatThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Review Date must be entered for completed hardship");

    }

    @ParameterizedTest
    @MethodSource("hardshipReviewStatusDataForNoValidationException")
    void validateHardshipReviewStatus_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        configureStubs();
        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @ParameterizedTest
    @MethodSource("newWorkReasonDataForValidationException")
    void validateHardshipReviewNewWorkReason(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertThatThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Review Reason must be entered for hardship");
    }

    @ParameterizedTest
    @MethodSource("solicitorDataForNoValidationException")
    void validateSolicitorDetails_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        configureStubs();
        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @ParameterizedTest
    @MethodSource("solicitorDataForValidationException")
    void validateSolicitorDetails_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertThatThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified");
    }

    @ParameterizedTest
    @MethodSource("deniedIncomeDataForValidationException")
    void validateDeniedIncome_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertThatThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        ).isInstanceOf(ValidationException.class)
                .hasMessage("Amount, Frequency, and Reason must be entered for each detail in section Medical Grounds");
    }

    @ParameterizedTest
    @MethodSource("deniedIncomeDataForNoValidationException")
    void validateDeniedIncome_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        configureStubs();
        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @ParameterizedTest
    @MethodSource("expenditureDataForValidationException")
    void validateExtraExpenditure_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertThatThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        ).isInstanceOf(ValidationException.class)
                .hasMessage(
                        "Amount, Frequency, and Reason must be entered for each detail in section Mortgage on additional Property");
    }

    @ParameterizedTest
    @MethodSource("expenditureDataForNoValidationException")
    void validateExtraExpenditure_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        configureStubs();
        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    @ParameterizedTest
    @MethodSource("progressionItemDataForValidationException")
    void validateProgressionItem_validationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {

        assertThatThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        ).isInstanceOf(ValidationException.class)
                .hasMessage(
                        "Date Taken, Response Required, and Date Required must be entered for each Action Taken in section Review Progress");
    }

    @ParameterizedTest
    @MethodSource("progressionItemDataForNoValidationException")
    void validateProgressionItem_noValidationException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        configureStubs();
        assertThatNoException().isThrownBy(
                () -> hardshipValidationService.checkHardship(apiPerformHardshipRequest, RequestType.CREATE)
        );
    }

    private static Stream<Arguments> apiPerformHardshipRequestDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(TestModelDataBuilder.getHardshipReview(),
                                                           TestModelDataBuilder.getHardshipMetadata()
                )));
    }

    private static Stream<Arguments> hardshipReviewStatusDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().withReviewDate(null),
                                                           new HardshipMetadata().withReviewStatus(
                                                                   HardshipReviewStatus.COMPLETE)
                             )
                ));
    }

    private static Stream<Arguments> hardshipReviewStatusDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview().withReviewDate(LocalDateTime.now()),
                        TestModelDataBuilder.getHardshipMetadata().withReviewStatus(HardshipReviewStatus.COMPLETE)
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview().withReviewDate(LocalDateTime.now()),
                        TestModelDataBuilder.getHardshipMetadata().withReviewStatus(HardshipReviewStatus.IN_PROGRESS)
                ))
        );
    }

    private static Stream<Arguments> newWorkReasonDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview(),
                                                           new HardshipMetadata().withReviewReason(null)
                )));
    }

    private static Stream<Arguments> solicitorDataForNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview()
                                                                   .withSolicitorCosts(new SolicitorCosts().withRate(
                                                                           BigDecimal.ZERO).withHours(BigDecimal.ZERO))
                                                                   .withReviewDate(LocalDateTime.now()),
                                                           TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                                                                   withSolicitorCosts(
                                                                           new SolicitorCosts().withRate(null)
                                                                                   .withHours(BigDecimal.ZERO))
                                                                   .withReviewDate(LocalDateTime.now()),
                                                           TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview()
                                                                   .withSolicitorCosts(null)
                                                                   .withReviewDate(LocalDateTime.now()),
                                                           TestModelDataBuilder.getHardshipMetadata()
                ))
        );
    }

    private static Stream<Arguments> solicitorDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                                                                   withSolicitorCosts(
                                                                           new SolicitorCosts().withRate(BigDecimal.ONE)
                                                                                   .withHours(BigDecimal.ZERO)),
                                                           new HardshipMetadata().withReviewReason(NewWorkReason.NEW)
                )),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                                                                   withSolicitorCosts(
                                                                           new SolicitorCosts().withRate(BigDecimal.ONE)
                                                                                   .withHours(null)),
                                                           new HardshipMetadata().withReviewReason(NewWorkReason.NEW)
                ))
        );
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
                        TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(null),
                        TestModelDataBuilder.getHardshipMetadata()
                ))
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
                        TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(List.of(TestModelDataBuilder.getDeniedIncome()
                                                                  .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                                                  .withFrequency(Frequency.ANNUALLY)
                                                                  .withAmount(BigDecimal.ONE)
                                                                  .withReasonNote(null))),
                        TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withDeniedIncome(List.of(TestModelDataBuilder.getDeniedIncome()
                                                                  .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                                                  .withFrequency(Frequency.ANNUALLY)
                                                                  .withAmount(null)
                                                                  .withReasonNote("test"))),
                        TestModelDataBuilder.getHardshipMetadata()
                ))
        );
    }

    private static Stream<Arguments> expenditureDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                                                      .withItemCode(
                                                                              ExtraExpenditureDetailCode.ADD_MORTGAGE)
                                                                      .withFrequency(null)
                                                                      .withAmount(BigDecimal.ONE)
                                                                      .withReasonCode(
                                                                              HardshipReviewDetailReason.ARRANGEMENT_IN_PLACE))),
                        TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                                                      .withItemCode(
                                                                              ExtraExpenditureDetailCode.ADD_MORTGAGE)
                                                                      .withFrequency(Frequency.ANNUALLY)
                                                                      .withAmount(BigDecimal.ONE)
                                                                      .withReasonCode(null))),
                        TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(List.of(TestModelDataBuilder.getExtraExpenditure()
                                                                      .withItemCode(
                                                                              ExtraExpenditureDetailCode.ADD_MORTGAGE)
                                                                      .withFrequency(Frequency.ANNUALLY)
                                                                      .withAmount(null)
                                                                      .withReasonCode(
                                                                              HardshipReviewDetailReason.ARRANGEMENT_IN_PLACE))),
                        TestModelDataBuilder.getHardshipMetadata()
                ))
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
                        TestModelDataBuilder.getHardshipMetadata()
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withExtraExpenditure(null),
                        TestModelDataBuilder.getHardshipMetadata()
                ))
        );
    }

    private static Stream<Arguments> progressionItemDataForValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(
                                        List.of(TestModelDataBuilder.getHardshipProgress()
                                                        .withAction(
                                                                HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                                        .withDateRequired(null)
                                                        .withResponse(
                                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                        .withDateTaken(LocalDateTime.now())))
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                                                   .withAction(
                                                                           HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                                                   .withDateRequired(LocalDateTime.now())
                                                                   .withResponse(null)
                                                                   .withDateTaken(LocalDateTime.now())))
                )),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                                                   .withAction(
                                                                           HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                                                   .withDateRequired(LocalDateTime.now())
                                                                   .withResponse(
                                                                           HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                                   .withDateTaken(null)))
                ))
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
                                             )
                             ),
                             Arguments.of(new ApiPerformHardshipRequest(
                                     TestModelDataBuilder.getMinimalHardshipReview(),
                                     TestModelDataBuilder.getHardshipMetadata().withProgressItems(null)
                             ))
                ),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata()
                                .withProgressItems(List.of(TestModelDataBuilder.getHardshipProgress()
                                                                   .withAction(
                                                                           HardshipReviewProgressAction.ADDITIONAL_EVIDENCE)
                                                                   .withDateRequired(LocalDateTime.now())
                                                                   .withResponse(
                                                                           HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                                   .withDateTaken(LocalDateTime.now())))
                ))
        );
    }
}
