package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HardshipReviewProgressResponse {

    FURTHER_RECEIVED("FURTHER RECEIVED", "Further Information received"),
    ORIGINAL_RECEIVED("ORIGINAL RECEIVED", "Original application received from HMCS"),
    ADDITIONAL_PROVIDED("ADDITIONAL PROVIDED", "Additional evidence provided");

    private String response;
    private String description;

}
