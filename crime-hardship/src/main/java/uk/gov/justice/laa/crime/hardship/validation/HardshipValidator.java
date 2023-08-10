package uk.gov.justice.laa.crime.hardship.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.exception.ValidationException;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
public class HardshipValidator {

    private Optional<Void> checkReviewDate(LocalDate reviewDate, LocalDate initAssDate) {
        if (reviewDate.isBefore(initAssDate)) {
            throw new ValidationException("Hardship review date precedes the initial assessment date");
        }

        return Optional.empty();
    }

    private Optional<Void> checkReviewDate(LocalDate reviewDate, LocalDate initAssDate, LocalDate fullAssDate) {
        if (reviewDate.isBefore(fullAssDate) || reviewDate.isBefore(initAssDate)) {
            throw new ValidationException("Hardship review date precedes the initial or full assessment dates");
        }

        return Optional.empty();
    }
}
