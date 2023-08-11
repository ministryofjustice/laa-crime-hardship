package uk.gov.justice.laa.crime.hardship.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewProgressAction;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewProgressResponse;

import java.time.LocalDateTime;


public record HardshipReviewProgress(

        Integer id,
        LocalDateTime dateRequested,
        LocalDateTime dateRequired,
        LocalDateTime dateCompleted,
        LocalDateTime timestamp,

        HardshipReviewProgressAction progressAction,
        HardshipReviewProgressResponse progressResponse,

        @JsonIgnore
        LocalDateTime dateCreated,
        @JsonIgnore
        LocalDateTime dateModified,
        @JsonIgnore
        LocalDateTime removedDate,
        @JsonIgnore
        String userCreated,
        @JsonIgnore
        String userModified
) {
    public LocalDateTime getTimestamp() {
        return dateModified != null ? dateModified : dateCreated;
    }
}
