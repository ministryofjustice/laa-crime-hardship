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
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.hardship.service.HardshipService;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/internal/v1/hardship")
@Tag(name = "Crime Hardship", description = "Rest API for Crime Hardship.")
public class HardshipController {

    private final HardshipMapper mapper;
    private final HardshipService hardshipService;

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
        return ResponseEntity.ok(hardshipService.calculateHardshipForDetail(request));
    }


    @GetMapping(value = "/hardshipReviewId", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Find Hardship review")
    @ApiResponse(responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiPerformHardshipResponse.class)
            )
    )
    @DefaultHTTPErrorResponse
    public ResponseEntity<ApiPerformHardshipResponse> find(
            @PathVariable int hardshipReviewId,
            @Parameter(description = "Used to trace calls between services")
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        return ResponseEntity.ok().build();
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
            ) @Valid @RequestBody ApiPerformHardshipRequest hardship,
            @Parameter(description = "Used to trace calls between services")
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        HardshipReviewDTO reviewDTO = preProcessRequest(hardship, RequestType.CREATE);
        // Call service methods
        return ResponseEntity.ok().build();
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
            ) @Valid @RequestBody ApiPerformHardshipRequest hardship,
            @Parameter(description = "Used to trace calls between services")
            @RequestHeader(value = "Laa-Transaction-Id", required = false) String laaTransactionId) {

        HardshipReviewDTO reviewDTO = preProcessRequest(hardship, RequestType.UPDATE);
        // Call service methods
        return ResponseEntity.ok().build();
    }

    private HardshipReviewDTO preProcessRequest(ApiPerformHardshipRequest hardship, RequestType requestType) {
        HardshipReviewDTO reviewDTO = HardshipReviewDTO.builder()
                .requestType(requestType).build();
        mapper.toDto(hardship, reviewDTO);
        return reviewDTO;
    }

}