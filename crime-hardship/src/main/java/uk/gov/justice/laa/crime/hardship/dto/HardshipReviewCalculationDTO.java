package uk.gov.justice.laa.crime.hardship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HardshipReviewCalculationDTO {

    private List<HardshipReviewCalculationDetail> hardshipReviewCalculationDetails;
    @Builder.Default
    private BigDecimal disposableIncome = BigDecimal.ZERO;
}
