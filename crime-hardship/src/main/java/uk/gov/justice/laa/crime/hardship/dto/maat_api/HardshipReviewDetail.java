package uk.gov.justice.laa.crime.hardship.dto.maat_api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewDetail {

    private Integer id;
    private Frequency frequency;
    private LocalDateTime dateReceived;
    private BigDecimal amount;
    private LocalDateTime dateDue;
    private String accepted;
    private String otherDescription;
    private String reasonNote;
    private HardshipReviewDetailType detailType;
    private HardshipReviewDetailCode detailCode;
    private HardshipReviewDetailReason detailReason;
    private LocalDateTime timestamp;

    @JsonIgnore()
    private LocalDateTime dateCreated;
    @JsonIgnore
    private LocalDateTime dateModified;
    @JsonIgnore
    private String description;
    @JsonIgnore
    private String reasonResponse;
    @JsonIgnore
    private String userModified;
    @JsonIgnore
    private String userCreated;
    @JsonIgnore
    private Boolean active;
    @JsonIgnore
    private LocalDateTime removedDate;

    public LocalDateTime getTimestamp() {
        return dateModified != null ? dateModified : dateCreated;
    }
}
