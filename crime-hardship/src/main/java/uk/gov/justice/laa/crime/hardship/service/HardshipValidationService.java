package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.DeniedIncome;
import uk.gov.justice.laa.crime.hardship.model.ExtraExpenditure;
import uk.gov.justice.laa.crime.hardship.model.HardshipProgress;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipValidationService {

    private final MaatCourtDataService maatCourtDataService;

    public static final String INCOMPLETE_ASSESSMENT_VALIDATION_MESSAGE =
            "Hardship review can only be entered after a completed assessment";
    public static final String HARDSHIP_REVIEW_DATE_VALIDATION_MESSAGE =
            "Hardship review date precedes the initial or full assessment date(s)";
    public static final String HARDSHIP_REVIEW_STATUS_VALIDATION_MESSAGE =
            "Review Date must be entered for completed hardship";
    public static final String NEW_WORK_REASON_VALIDATION_MESSAGE = "Review Reason must be entered for hardship";
    public static final String SOLICITOR_DETAILS_VALIDATION_MESSAGE =
            "Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified";
    public static final String EXPENDITURE_OR_DENIED_INCOME_VALIDATION_MESSAGE =
            "Amount, Frequency, and Reason must be entered for each detail in section ";
    public static final String PROGRESSION_ITEMS_VALIDATION_MESSAGE =
            "Date Taken, Response Required, and Date Required must be entered for each Action Taken in section Review Progress";

    private static boolean progressionItemWithoutRequiredDateOrResponseOrDateTaken(HardshipProgress progression) {
        return (nonNull(progression.getAction()) &&
                (isNull(progression.getDateRequired()) || isNull(progression.getResponse()) ||
                        isNull(progression.getDateTaken())));
    }

    private static boolean expenditureWithoutAmountOrFrequencyOrReasonCode(ExtraExpenditure expenditure) {
        return (nonNull(expenditure.getItemCode()) &&
                (isNull(expenditure.getAmount()) || isNull(expenditure.getFrequency()) ||
                        isNull(expenditure.getReasonCode())));
    }

    private static boolean hardshipStatusIsCompleteWithoutReviewDate(
            ApiPerformHardshipRequest apiPerformHardshipRequest) {
        return ((apiPerformHardshipRequest.getHardshipMetadata().getReviewStatus() == HardshipReviewStatus.COMPLETE)
                && isNull(apiPerformHardshipRequest.getHardship().getReviewDate()));
    }

    private static boolean solicitorRateSpecifiedWithoutSolicitorHours(BigDecimal solicitorRate,
                                                                       BigDecimal solicitorHours) {
        return (solicitorRate.compareTo(BigDecimal.ZERO) > 0) && (solicitorHours.intValue() == 0);
    }

    private static boolean deniedIncomeWithoutAmountOrFrequencyOrReasonNote(DeniedIncome deniedIncome) {
        return (nonNull(deniedIncome.getItemCode()) &&
                (isNull(deniedIncome.getAmount()) || isNull(deniedIncome.getFrequency()) ||
                        StringUtils.isEmpty(deniedIncome.getReasonNote())));
    }

    public void checkHardship(final ApiPerformHardshipRequest apiPerformHardshipRequest) {
        validateReviewDate(apiPerformHardshipRequest);
        validateHardshipReviewStatus(apiPerformHardshipRequest);
        validateHardshipReviewNewWorkReason(apiPerformHardshipRequest);
        validateSolicitorDetails(apiPerformHardshipRequest);
        validateDeniedIncome(apiPerformHardshipRequest);
        validateExpenditure(apiPerformHardshipRequest);
        validateProgressionItems(apiPerformHardshipRequest);
    }

    public void validateReviewDate(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        FinancialAssessmentDTO financialAssessment =
                maatCourtDataService.getFinancialAssessment(
                        apiPerformHardshipRequest.getHardshipMetadata().getFinancialAssessmentId()
                );

        if (financialAssessment.getDateCompleted() != null) {
            throw new ValidationException(INCOMPLETE_ASSESSMENT_VALIDATION_MESSAGE);
        }

        var reviewDate = apiPerformHardshipRequest.getHardship().getReviewDate();
        var initialAssessmentDate = financialAssessment.getInitialAssessmentDate();
        var fullAssessmentDate = financialAssessment.getFullAssessmentDate();
        var assessmentDate = fullAssessmentDate != null ? fullAssessmentDate : initialAssessmentDate;

        if (reviewDate.isBefore(assessmentDate)) {
            throw new ValidationException(HARDSHIP_REVIEW_DATE_VALIDATION_MESSAGE);
        }
    }

    private void validateHardshipReviewStatus(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        if (hardshipStatusIsCompleteWithoutReviewDate(apiPerformHardshipRequest)) {
            throw new ValidationException(HARDSHIP_REVIEW_STATUS_VALIDATION_MESSAGE);
        }
    }

    private void validateHardshipReviewNewWorkReason(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        if (isNull(apiPerformHardshipRequest.getHardshipMetadata().getReviewReason())) {
            throw new ValidationException(NEW_WORK_REASON_VALIDATION_MESSAGE);
        }
    }

    private void validateSolicitorDetails(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        var solicitorCosts = apiPerformHardshipRequest.getHardship().getSolicitorCosts();
        var solicitorRate = BigDecimal.ZERO;
        var solicitorHours = BigDecimal.ZERO;
        if (solicitorCosts != null) {
            solicitorRate = Optional.ofNullable(solicitorCosts.getRate()).orElse(BigDecimal.ZERO);
            solicitorHours = Optional.ofNullable(solicitorCosts.getHours()).orElse(BigDecimal.ZERO);
        }
        if (solicitorRateSpecifiedWithoutSolicitorHours(solicitorRate, solicitorHours)) {
            throw new ValidationException(SOLICITOR_DETAILS_VALIDATION_MESSAGE);
        }
    }

    private void validateDeniedIncome(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<DeniedIncome> deniedIncomes = apiPerformHardshipRequest.getHardship().getDeniedIncome();
        Optional.ofNullable(deniedIncomes).orElse(List.of()).forEach(deniedIncome -> {
            if (deniedIncomeWithoutAmountOrFrequencyOrReasonNote(deniedIncome)) {
                throw new ValidationException(
                        EXPENDITURE_OR_DENIED_INCOME_VALIDATION_MESSAGE + deniedIncome.getItemCode().getDescription());
            }
        });
    }

    private void validateExpenditure(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<ExtraExpenditure> expenditures = apiPerformHardshipRequest.getHardship().getExtraExpenditure();
        Optional.ofNullable(expenditures).orElse(List.of()).forEach(expenditure -> {
            if (expenditureWithoutAmountOrFrequencyOrReasonCode(expenditure)) {
                throw new ValidationException(
                        EXPENDITURE_OR_DENIED_INCOME_VALIDATION_MESSAGE + expenditure.getItemCode().getDescription());
            }
        });
    }

    private void validateProgressionItems(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<HardshipProgress> progressionItems = apiPerformHardshipRequest.getHardshipMetadata().getProgressItems();
        Optional.ofNullable(progressionItems).orElse(List.of()).forEach(progression -> {
            if (progressionItemWithoutRequiredDateOrResponseOrDateTaken(progression)) {
                throw new ValidationException(PROGRESSION_ITEMS_VALIDATION_MESSAGE);
            }
        });
    }
}
