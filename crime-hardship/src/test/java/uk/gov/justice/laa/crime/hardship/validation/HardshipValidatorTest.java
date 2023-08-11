package uk.gov.justice.laa.crime.hardship.validation;

import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class HardshipValidatorTest {
    public static final LocalDate TODAYS_DATE = LocalDate.now();
    public static final LocalDate YESTERDAYS_DATE = LocalDate.now().minusDays(1);
    public static final LocalDate DAY_BEFORE_YESTERDAY_DATE = LocalDate.now().minusDays(2);
    public final HardshipValidator hardshipValidator = new HardshipValidator();

    @Test
    void givenReviewDateIsAfterAssDate_whenCheckReviewDateIsInvoked_thenEmptyIsReturned() {
        assertThat(hardshipValidator.checkReviewDate(TODAYS_DATE, YESTERDAYS_DATE)).isEmpty();
    }

    @Test
    void givenReviewDateIsBeforeAssDate_whenCheckReviewDateIsInvoked_thenExceptionIsRaised() {
        assertThatThrownBy(() -> hardshipValidator.checkReviewDate(YESTERDAYS_DATE, TODAYS_DATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review date precedes the initial assessment date");
    }

    @Test
    void givenReviewDateIsAfterBothAssDates_whenCheckReviewDateIsInvoked_thenEmptyIsReturned() {
        assertThat(hardshipValidator.checkReviewDate(TODAYS_DATE, DAY_BEFORE_YESTERDAY_DATE, YESTERDAYS_DATE)).isEmpty();
    }

    @Test
    void givenReviewDateIsBeforeFullAssDate_whenCheckReviewDateIsInvoked_thenExceptionIsRaised() {
        assertThatThrownBy(() -> hardshipValidator.checkReviewDate(YESTERDAYS_DATE, DAY_BEFORE_YESTERDAY_DATE, TODAYS_DATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review date precedes the initial or full assessment dates");
    }

    @Test
    void givenReviewDateIsBeforeInitAssDate_whenCheckReviewDateIsInvoked_thenExceptionIsRaised() {
        assertThatThrownBy(() -> hardshipValidator.checkReviewDate(YESTERDAYS_DATE, TODAYS_DATE, DAY_BEFORE_YESTERDAY_DATE))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Hardship review date precedes the initial or full assessment dates");
    }
}
