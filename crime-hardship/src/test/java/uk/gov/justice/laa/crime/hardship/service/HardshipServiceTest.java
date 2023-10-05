package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.common.Constants;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.PersistHardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class HardshipServiceTest {

    @InjectMocks
    private HardshipService hardshipService;

    @Spy
    private PersistHardshipMapper mapper;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private HardshipCalculationService calculationService;

    @Test
    void givenValidParameters_whenCreateIsInvoked_thenHardshipIsPersisted() {
        HardshipReviewDTO reviewDTO = HardshipReviewDTO.builder()
                .hardship(TestModelDataBuilder.getHardshipReview())
                .hardshipMetadata(TestModelDataBuilder.getHardshipMetadata())
                .build();

        HardshipResult expectedResult = TestModelDataBuilder.getHardshipResult(HardshipReviewResult.PASS);

        when(calculationService.calculateHardship(any(HardshipReview.class), any(BigDecimal.class)))
                .thenReturn(expectedResult);

        when(maatCourtDataService.persistHardship(any(ApiPersistHardshipRequest.class), anyString(),
                                                  eq(RequestType.CREATE)
        ))
                .thenReturn(new ApiPersistHardshipResponse()
                                    .withId(1000)
                                    .withDateCreated(LocalDateTime.now())
                );

        HardshipReviewDTO result = hardshipService.create(reviewDTO, Constants.LAA_TRANSACTION_ID);
        assertThat(result.getHardshipResult()).isEqualTo(expectedResult);
    }
}
