package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HardshipReviewDetailReasonsTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(HardshipReviewDetailReasons.getFrom(null)).isNull();
    }

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(HardshipReviewDetailReasons.getFrom("Evidence Supplied")).isEqualTo(HardshipReviewDetailReasons.EVIDENCE_SUPPLIED);
    }

    @Test
    void valueOfCurrentStatusFromString_nullParameter_ReturnsNull() {
        assertThatThrownBy(
                () -> HardshipReviewDetailReasons.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> HardshipReviewDetailReasons.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("Evidence Supplied").isEqualTo(HardshipReviewDetailReasons.EVIDENCE_SUPPLIED.getReason());
        assertThat("EXPENDITURE").isEqualTo(HardshipReviewDetailReasons.EVIDENCE_SUPPLIED.getType());
    }

}