package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HardshipReviewDetailCodeTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(ExtraExpenditureDetailCode.getFrom(null)).isNull();
    }

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(ExtraExpenditureDetailCode.getFrom("BAILIFF")).isEqualTo(ExtraExpenditureDetailCode.BAILIFF);
    }

    @Test
    void valueOfCurrentStatusFromString_nullParameter_ReturnsNull() {
        assertThatThrownBy(
                () -> ExtraExpenditureDetailCode.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("ADD MORTGAGE").isEqualTo(ExtraExpenditureDetailCode.ADD_MORTGAGE.getCode());
        assertThat("Car Loan").isEqualTo(ExtraExpenditureDetailCode.CAR_LOAN.getDescription());
    }

}
