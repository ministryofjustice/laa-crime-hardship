package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Frequency {

    WEEKLY("WEEKLY", "Weekly", 52),
    TWO_WEEKLY("2WEEKLY", "2 Weekly", 26),
    FOUR_WEEKLY("4WEEKLY", "4 Weekly", 13),
    MONTHLY("MONTHLY", "Monthly", 12),
    ANNUALLY("ANNUALLY", "Annually", 1);

    @JsonValue
    private String code;
    private String description;
    private int annualWeighting;

}
