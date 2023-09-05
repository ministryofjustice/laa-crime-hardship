package uk.gov.justice.laa.crime.hardship.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SoftAssertionsExtension.class)
class HardshipMapperTest {

    HardshipMapper mapper = new HardshipMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenHardshipReviewDTO_whenFromDtoIsInvoked_thenResponseIsMapped() {

        HardshipReview hardship = new HardshipReview()
                .withHardshipReviewId(1000)
                .withTotalAnnualDisposableIncome(BigDecimal.valueOf(500));

        HardshipResult result = HardshipResult.builder()
                .result(HardshipReviewResult.PASS)
                .postHardshipDisposableIncome(BigDecimal.valueOf(250))
                .build();

        HardshipReviewDTO reviewDTO = HardshipReviewDTO.builder()
                .hardship(hardship)
                .hardshipResult(result)
                .build();

        ApiPerformHardshipResponse response = mapper.fromDto(reviewDTO);

        softly.assertThat(response.getHardshipReviewId())
                .isEqualTo(hardship.getHardshipReviewId());

        softly.assertThat(response.getDisposableIncome())
                .isEqualTo(hardship.getTotalAnnualDisposableIncome());

        softly.assertThat(response.getPostHardshipDisposableIncome())
                .isEqualTo(result.getPostHardshipDisposableIncome());

        softly.assertThat(response.getReviewResult())
                .isEqualTo(result.getResult());
    }

    @Test
    void givenHardshipReviewDTO_whenToDtoIsInvoked_thenDtoIsMapped() {
        HardshipReview hardship = new HardshipReview()
                .withHardshipReviewId(1000);

        HardshipReviewDTO reviewDTO = new HardshipReviewDTO();

        mapper.toDto(hardship, reviewDTO);

        assertThat(reviewDTO.getHardship().getHardshipReviewId())
                .isEqualTo(hardship.getHardshipReviewId());
    }
}
