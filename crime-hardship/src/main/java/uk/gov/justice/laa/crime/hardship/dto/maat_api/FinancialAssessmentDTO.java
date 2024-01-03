package uk.gov.justice.laa.crime.hardship.dto.maat_api;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinancialAssessmentDTO {
    private Integer id;
    private Integer repId;
    private LocalDateTime dateCompleted;
    private LocalDateTime initialAssessmentDate;
    private LocalDateTime fullAssessmentDate;
}
