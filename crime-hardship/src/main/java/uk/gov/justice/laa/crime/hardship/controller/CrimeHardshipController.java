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
import org.springframework.web.bind.annotation.*;
import uk.gov.justice.laa.crime.hardship.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.hardship.model.ApiCreateHardshipReviewRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiHardshipReviewResponse;
import uk.gov.justice.laa.crime.hardship.model.ApiUpdateHardshipReviewRequest;
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
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiStatelessCalculateHardshipByDetailResponse> calculateHardshipForDetail(
            @Parameter(description = "Calculate Crime Hardship For Detail",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiStatelessCalculateHardshipByDetailRequest.class)
                    )
            ) @Valid @RequestBody ApiStatelessCalculateHardshipByDetailRequest request) {
        return ResponseEntity.ok(hardshipService.calculateHardshipForDetail(request));
    }

    @GetMapping(value = "/hardshipReviewId", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiHardshipReviewResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiHardshipReviewResponse> find(
            @PathVariable int hardshipReviewId,
            @Parameter(description = "Used to trace calls between services")
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiHardshipReviewResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiHardshipReviewResponse> create(
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCreateHardshipReviewRequest.class)
                    )
            ) @Valid @RequestBody ApiCreateHardshipReviewRequest request,
            @Parameter(description = "Used to trace calls between services")
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        return ResponseEntity.ok().build();
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiHardshipReviewResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiHardshipReviewResponse> update(
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiUpdateHardshipReviewRequest.class)
                    )
            ) @Valid @RequestBody ApiUpdateHardshipReviewRequest request,
            @Parameter(description = "Used to trace calls between services")
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        return ResponseEntity.ok().build();
    }

}
