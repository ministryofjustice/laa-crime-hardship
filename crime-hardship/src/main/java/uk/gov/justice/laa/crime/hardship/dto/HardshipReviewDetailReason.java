package uk.gov.justice.laa.crime.hardship.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewDetailReason {

    private String id;
    private String reason;
    @JsonIgnore
    private HardshipReviewDetailType detailType;
    @JsonIgnore
    private String forceNote;
    @JsonIgnore
    private LocalDateTime dateCreated;
    @JsonIgnore
    private String userCreated;
    @JsonIgnore
    private LocalDateTime dateModified;
    @JsonIgnore
    private String userModified;
    @JsonIgnore
    private String accepted;
}
