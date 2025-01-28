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
import uk.gov.justice.laa.crime.annotation.DefaultHTTPErrorResponse;
import uk.gov.justice.laa.crime.common.model.hardship.*;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.hardship.service.CrimeMeansAssessmentService;
import uk.gov.justice.laa.crime.hardship.service.HardshipCalculationService;
import uk.gov.justice.laa.crime.hardship.service.HardshipService;
import uk.gov.justice.laa.crime.hardship.validation.HardshipValidationService;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/hardship")
@Tag(name = "Crime Hardship", description = "Rest API for Crime Hardship.")
public class HardshipController {

    private final HardshipMapper mapper;
    private final HardshipService hardshipService;
    private final CrimeMeansAssessmentService crimeMeansAssessmentService;
    private final HardshipValidationService hardshipValidationService;
    private final HardshipCalculationService hardshipCalculationService;

    @PostMapping(value = "/calculate-hardship-for-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Calculate Crime Hardship for Detail")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiCalculateHardshipByDetailResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiCalculateHardshipByDetailResponse> calculateHardshipForDetail(
            @Parameter(description = "Calculate Crime Hardship For Detail",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCalculateHardshipByDetailRequest.class)
                    )
            ) @Valid @RequestBody ApiCalculateHardshipByDetailRequest request) {

        return ResponseEntity.ok(
                hardshipCalculationService.calculateHardshipForDetail(
                        request.getRepId(),
                        HardshipReviewDetailType.valueOf(request.getDetailType())
                )
        );
    }

    @GetMapping(value = "/{hardshipReviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiPerformHardshipResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiFindHardshipResponse> find(
            @PathVariable int hardshipReviewId) {
        log.info("Request received to retrieve hardship review: {}", hardshipReviewId);
        throw new RuntimeException("Testing Sentry");
        //return ResponseEntity.ok(hardshipService.find(hardshipReviewId));
    }


    @PostMapping(value = "/calculate-hardship", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Generic Client Agnostic Calculate Crime Hardship")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiCalculateHardshipResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiCalculateHardshipResponse> calculateHardship(
            @Parameter(description = "Generic Client Agnostic Calculate Crime Hardship",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiCalculateHardshipRequest.class)
                    )
            ) @Valid @RequestBody ApiCalculateHardshipRequest request) {

        BigDecimal fullThreshold = crimeMeansAssessmentService
                .getFullAssessmentThreshold(request.getHardship().getReviewDate());

        HardshipResult hardshipResult = hardshipCalculationService.calculateHardship(
                request.getHardship(), fullThreshold);

        return ResponseEntity.ok(new ApiCalculateHardshipResponse()
                .withReviewResult(hardshipResult.getResult())
                .withPostHardshipDisposableIncome(hardshipResult.getPostHardshipDisposableIncome()));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiPerformHardshipResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiPerformHardshipResponse> create(
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiPerformHardshipRequest.class)
                    )
            ) @Valid @RequestBody ApiPerformHardshipRequest hardship) {

        HardshipReviewDTO reviewDTO = preProcessRequest(hardship, RequestType.CREATE);
        reviewDTO = hardshipService.create(reviewDTO);
        return ResponseEntity.ok(mapper.fromDto(reviewDTO));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Update Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiPerformHardshipResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiPerformHardshipResponse> update(
            @Parameter(description = "JSON object containing Hardship information",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiPerformHardshipRequest.class)
                    )
            ) @Valid @RequestBody ApiPerformHardshipRequest hardship) {
        HardshipReviewDTO reviewDTO = preProcessRequest(hardship, RequestType.UPDATE);
        reviewDTO = hardshipService.update(reviewDTO);
        return ResponseEntity.ok(mapper.fromDto(reviewDTO));
    }

    @PatchMapping(value = "/{hardshipReviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Rollback Hardship review")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @DefaultHTTPErrorResponse
    public ResponseEntity<Void> rollback(@PathVariable int hardshipReviewId) {
        log.info("Received request to rollback Hardship Review with Id: [{}]", hardshipReviewId);
        hardshipService.rollback(hardshipReviewId);
        return ResponseEntity.ok().build();
    }

    private HardshipReviewDTO preProcessRequest(ApiPerformHardshipRequest hardship, RequestType requestType) {
        HardshipReviewDTO reviewDTO = HardshipReviewDTO.builder()
                .requestType(requestType).build();
        hardshipValidationService.checkHardship(hardship, requestType);
        mapper.toDto(hardship, reviewDTO);
        return reviewDTO;
    }
}
