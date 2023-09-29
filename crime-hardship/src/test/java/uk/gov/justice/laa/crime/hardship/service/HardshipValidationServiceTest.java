package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.HardshipMetadata;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.NewWorkReason;

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
    @Test
    void validateHardshipReviewStatus() {
        ValidationException validationException = assertThrows(ValidationException.class, ()->hardshipValidationService.
                checkHardship(new ApiPerformHardshipRequest(new HardshipReview().withReviewDate(null),
                        new HardshipMetadata().withReviewStatus(HardshipReviewStatus.COMPLETE))));
        assertEquals("Review Date must be entered for completed hardship", validationException.getMessage());
    }

    @Test
    void validateHardshipReviewStatusWithNoValidationException() {
        assertDoesNotThrow(()->hardshipValidationService.
                checkHardship(new ApiPerformHardshipRequest(TestModelDataBuilder.getMinimalHardshipReview().withReviewDate(LocalDateTime.now()),
                        TestModelDataBuilder.getHardshipMetadata().withReviewStatus(HardshipReviewStatus.COMPLETE))));
        assertDoesNotThrow(()->hardshipValidationService.
                checkHardship(new ApiPerformHardshipRequest(TestModelDataBuilder.getMinimalHardshipReview().withReviewDate(LocalDateTime.now()),
                        TestModelDataBuilder.getHardshipMetadata().withReviewStatus(HardshipReviewStatus.IN_PROGRESS))));
    }

    @Test
    void testHardshipValidationServiceWithNoValidationException() {
        assertDoesNotThrow(()->hardshipValidationService.
                checkHardship(new ApiPerformHardshipRequest(TestModelDataBuilder.getHardshipReview(),
                        TestModelDataBuilder.getHardshipMetadata())));
    }

    @Test
    void validateHardshipReviewNewWorkReason() {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(new ApiPerformHardshipRequest(new HardshipReview(),
                        new HardshipMetadata().withReviewReason(null))));
        assertEquals("Review Reason must be entered for hardship", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("validateSolicitorDetailsData")
    void validateSolicitorDetails(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(apiPerformHardshipRequest));
        assertEquals("Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("validateFundingSourcesData")
    void validateFundingSources(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ValidationException validationException =  assertThrows(ValidationException.class, () -> hardshipValidationService.
                checkHardship(apiPerformHardshipRequest));
        assertEquals("Amount and Date Expected must be entered for each detail in section Funding Source", validationException.getMessage());
    }

    @ParameterizedTest
    @MethodSource("validateFundingSourcesWithNoValidationException")
    void validateFundingSourcesWithNoException(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        assertDoesNotThrow(()->hardshipValidationService.checkHardship(apiPerformHardshipRequest));
    }

    private static Stream<Arguments> validateFundingSourcesWithNoValidationException() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription(null)
                                        .withAmount(BigDecimal.ONE).withDueDate(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("")
                                        .withAmount(null).withDueDate(LocalDateTime.now()))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("Funding Source")
                                        .withAmount(BigDecimal.ONE).withDueDate(LocalDateTime.now()))),
                        TestModelDataBuilder.getHardshipMetadata()))
        );
    }


    private static Stream<Arguments> validateFundingSourcesData() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("Funding Source")
                                        .withAmount(BigDecimal.ZERO).withDueDate(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("Funding Source")
                                        .withAmount(null).withDueDate(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("Funding Source")
                                        .withAmount(BigDecimal.ZERO).withDueDate(LocalDateTime.now()))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("Funding Source")
                                        .withAmount(BigDecimal.ONE).withDueDate(null))),
                        TestModelDataBuilder.getHardshipMetadata())),
                Arguments.of(new ApiPerformHardshipRequest(
                        TestModelDataBuilder.getMinimalHardshipReview()
                                .withOtherFundingSources(List.of(TestModelDataBuilder.getOtherFundingSources()
                                        .withDescription("Funding Source")
                                        .withAmount(null).withDueDate(LocalDateTime.now()))),
                        TestModelDataBuilder.getHardshipMetadata()))
        );
    }

    private static Stream<Arguments> validateSolicitorDetailsData() {
        return Stream.of(
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(0)),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW))),
                Arguments.of(new ApiPerformHardshipRequest(new HardshipReview().
                        withSolicitorCosts(new SolicitorCosts().withRate(BigDecimal.ONE).withHours(null)),
                        new HardshipMetadata().withReviewReason(NewWorkReason.NEW))));
    }

}
