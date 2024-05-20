package uk.gov.justice.laa.crime.hardship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipMetadata;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.enums.RequestType;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewDTO {
    private RequestType requestType;
    private HardshipReview hardship;
    private HardshipMetadata hardshipMetadata;
    private HardshipResult hardshipResult;
}
