package uk.gov.justice.laa.crime.hardship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.CourtType;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HardshipReviewDTO {

    private Integer id;
    private Integer repId;
    private Integer cmuId;
    private CourtType courtType;
    private HardshipReviewStatus reviewStatus;
    private LocalDateTime reviewDate;
    private LocalDateTime dateCreated;
    private ApiUserSession userSession;
    private NewWorkReason reviewReason;
    private String notes;
    private String decisionNotes;
    private Integer financialAssessmentId;
    private BigDecimal totalAnnualDisposableIncome;
    private SolicitorCosts solicitorCosts;
    private List<OtherFundingSources> otherFundingSources;
    private List<ExtraExpenditure> extraExpenditure;
    private List<DeniedIncome> deniedIncome;
    private List<HardshipProgress> progressItems;
    private HardshipReviewResult reviewResult;
    private BigDecimal postHardshipDisposableIncome;

}
