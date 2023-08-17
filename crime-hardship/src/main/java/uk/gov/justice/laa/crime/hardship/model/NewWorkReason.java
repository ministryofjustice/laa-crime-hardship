package uk.gov.justice.laa.crime.hardship.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDateTime;


public record NewWorkReason(

        @JsonValue
        String code,
        String type,
        String description,
        LocalDateTime dateCreated,
        String userCreated,
        LocalDateTime dateModified,
        String userModified,
        Integer sequence,
        String enabled,
        String raGroup,
        String initialDefault
) {
}
