package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class HardshipReviewDetailCodeTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(HardshipReviewDetailCode.getFrom("")).isNull();
    }

    @Test
    void givenAValidCode_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(HardshipReviewDetailCode.getFrom("UNSECURED LOAN"))
                .isEqualTo(HardshipReviewDetailCode.UNSECURED_LOAN);
    }

    @Test
    void givenInvalidCode_whenGetFromIsInvoked_thenExceptionIsRaised() {
        assertThatThrownBy(() -> HardshipReviewDetailCode.getFrom("INVALID CODE"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
