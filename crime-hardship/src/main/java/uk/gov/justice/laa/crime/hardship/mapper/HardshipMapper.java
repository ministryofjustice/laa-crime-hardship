package uk.gov.justice.laa.crime.hardship.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;

import static java.util.Optional.ofNullable;

@Component
public class HardshipMapper implements RequestMapper<ApiPerformHardshipResponse, HardshipReviewDTO>,
        ResponseMapper<ApiPerformHardshipRequest, HardshipReviewDTO> {

    public ApiPerformHardshipResponse fromDto(HardshipReviewDTO reviewDTO) {

        HardshipReview hardship = reviewDTO.getHardship();
        HardshipResult hardshipResult = reviewDTO.getHardshipResult();

        return new ApiPerformHardshipResponse()
                .withReviewResult(ofNullable(hardshipResult)
                        .map(HardshipResult::getResult).orElse(null))
                .withDisposableIncome(hardship.getTotalAnnualDisposableIncome())
                .withHardshipReviewId(reviewDTO.getHardshipMetadata().getHardshipReviewId())
                .withPostHardshipDisposableIncome(ofNullable(hardshipResult)
                        .map(HardshipResult::getPostHardshipDisposableIncome).orElse(null));
    }

    public void toDto(ApiPerformHardshipRequest request, HardshipReviewDTO reviewDTO) {
        reviewDTO.setHardship(request.getHardship());
        reviewDTO.setHardshipMetadata(request.getHardshipMetadata());
    }
}
