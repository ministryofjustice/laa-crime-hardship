package uk.gov.justice.laa.crime.hardship.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class HardshipReviewCalculationDTO {

    private List<HardshipReviewCalculationDetail> hardshipReviewCalculationDetails;
    private BigDecimal disposableIncome;
}
