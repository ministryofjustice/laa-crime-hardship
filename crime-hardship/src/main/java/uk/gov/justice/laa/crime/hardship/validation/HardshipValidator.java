package uk.gov.justice.laa.crime.hardship.validation;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.exception.ValidationException;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class HardshipValidator {

    protected Optional<Void> checkReviewDate(LocalDate reviewDate, LocalDate initialAssessmentDate) {
        if (reviewDate.isBefore(initialAssessmentDate)) {
            throw new ValidationException("Hardship review date precedes the initial assessment date");
        }

        return Optional.empty();
    }

    protected Optional<Void> checkReviewDate(LocalDate reviewDate, LocalDate initialAssessmentDate,
                                             LocalDate fullAssessmentDate) {
        if (reviewDate.isBefore(fullAssessmentDate) || reviewDate.isBefore(initialAssessmentDate)) {
            throw new ValidationException("Hardship review date precedes the initial or full assessment dates");
        }

        return Optional.empty();
    }
}
