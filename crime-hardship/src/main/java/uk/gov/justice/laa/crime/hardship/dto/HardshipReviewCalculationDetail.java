package uk.gov.justice.laa.crime.hardship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardshipReviewCalculationDetail {

    private HardshipReviewDetailType detailType;
    private Frequency frequency;
    private BigDecimal amount;
    private String accepted;

}
