package uk.gov.justice.laa.crime.hardship.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.crime.hardship.dto.ErrorDTO;
import uk.gov.justice.laa.crime.hardship.model.stateless.ApiStatelessCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.stateless.ApiStatelessCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.service.HardshipService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/hardship")
@Tag(name = "Crime Hardship", description = "Rest API for Crime Hardship.")
public class CrimeHardshipController {

    private final HardshipService hardshipService;

    @PostMapping(value = "/calculate-hardship-for-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Crime Hardship for Detail")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiStatelessCalculateHardshipByDetailResponse.class)
            )
    )
    @ApiResponse(responseCode = "400",
            description = "Bad Request.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    @ApiResponse(responseCode = "500",
            description = "Server Error.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ResponseEntity<ApiStatelessCalculateHardshipByDetailResponse> calculateHardshipForDetail(
            @Parameter(description = "Calculate Crime Hardship For Detail",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiStatelessCalculateHardshipByDetailRequest.class)
                    )
            ) @Valid @RequestBody ApiStatelessCalculateHardshipByDetailRequest request) {
        return ResponseEntity.ok(hardshipService.calculateHardshipForDetail(request));
    }

}
