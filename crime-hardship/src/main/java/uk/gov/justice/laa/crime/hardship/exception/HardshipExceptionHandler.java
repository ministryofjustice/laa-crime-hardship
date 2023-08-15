package uk.gov.justice.laa.crime.hardship.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.gov.justice.laa.crime.hardship.dto.ErrorDTO;

@Slf4j
@RestControllerAdvice
public class HardshipExceptionHandler {
    private static ResponseEntity<ErrorDTO> buildErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(ErrorDTO.builder().code(status.toString()).message(message).build(), status);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(ValidationException exception) {
        log.error("Validation exception: {}", exception.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
