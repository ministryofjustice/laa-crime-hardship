package uk.gov.justice.laa.crime.hardship.dto.maat_api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewProgressAction;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewProgressResponse;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewProgress {

    private Integer id;
    private LocalDateTime dateRequested;
    private LocalDateTime dateRequired;
    private LocalDateTime dateCompleted;
    private LocalDateTime timestamp;

    private HardshipReviewProgressAction progressAction;
    private HardshipReviewProgressResponse progressResponse;

    @JsonIgnore
    private LocalDateTime dateCreated;
    @JsonIgnore
    private LocalDateTime dateModified;
    @JsonIgnore
    private LocalDateTime removedDate;
    @JsonIgnore
    private String userCreated;
    @JsonIgnore
    private String userModified;

    public LocalDateTime getTimestamp() {
        return dateModified != null ? dateModified : dateCreated;
    }
}
