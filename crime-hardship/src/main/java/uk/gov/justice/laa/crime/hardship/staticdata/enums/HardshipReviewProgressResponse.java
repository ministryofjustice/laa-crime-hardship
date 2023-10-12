package uk.gov.justice.laa.crime.hardship.staticdata.enums;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum HardshipReviewProgressResponse {

    FURTHER_RECEIVED("FURTHER RECEIVED", "Further Information received"),
    ORIGINAL_RECEIVED("ORIGINAL RECEIVED", "Original application received from HMCS"),
    ADDITIONAL_PROVIDED("ADDITIONAL PROVIDED", "Additional evidence provided");

    @JsonPropertyDescription("This will have the hardship review progress response")
    @JsonValue
    private String response;
    private String description;

    @JsonCreator
    public static HardshipReviewProgressResponse getValues(@JsonProperty("response") String response, @JsonProperty("description") String description) {
        return getFrom(response);
    }

    public static HardshipReviewProgressResponse getFrom(String response) {
        if (StringUtils.isBlank(response)) return null;

        return Stream.of(HardshipReviewProgressResponse.values())
                .filter(hrpr -> hrpr.response.equals(response))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("hardship review progress response with type: %s does not exist.", response)));
    }
}
