package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDTO;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewResultDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.FULL_THRESHOLD;
import static uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder.getHardshipReviewCalculationDTO;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class HardshipServiceTest {

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private HardshipService hardshipService;

    @Test
    void givenValidHardshipReviewCalculationDTOWithDetailTypeExpenditure_whenCalculateHardshipIsInvoked_thenHardshipReviewResultDTOIsReturned() {
        HardshipReviewCalculationDTO hardshipReviewCalculationDTO = getHardshipReviewCalculationDTO(EXPENDITURE);
        HardshipReviewResultDTO response = hardshipService.calculateHardship(hardshipReviewCalculationDTO, FULL_THRESHOLD);
        assertThat(response.getDisposableIncomeAfterHardship()).isEqualTo(BigDecimal.valueOf(-3320.0));
        assertThat(response.getHardshipSummary()).isEqualTo(BigDecimal.valueOf(8320.0));
        assertThat(response.getHardshipReviewResult()).isEqualTo("PASS");
    }

    @Test
    void givenValidHardshipReviewCalculationDTOWithDetailTypeIncome_whenCalculateHardshipIsInvoked_thenHardshipReviewResultDTOIsReturned() {
        HardshipReviewCalculationDTO hardshipReviewCalculationDTO = getHardshipReviewCalculationDTO(INCOME);
        HardshipReviewResultDTO response = hardshipService.calculateHardship(hardshipReviewCalculationDTO, FULL_THRESHOLD);
        assertThat(response.getDisposableIncomeAfterHardship()).isEqualTo(BigDecimal.valueOf(3000.0));
        assertThat(response.getHardshipSummary()).isEqualTo(BigDecimal.valueOf(2000.0));
        assertThat(response.getHardshipReviewResult()).isEqualTo("PASS");
    }

    @Test
    void givenValidHardshipReviewCalculationDTOWithMultipleDetailTypes_whenCalculateHardshipIsInvoked_thenHardshipReviewResultDTOIsReturned() {
        HardshipReviewCalculationDTO hardshipReviewCalculationDTO = getHardshipReviewCalculationDTO( EXPENDITURE, SOL_COSTS);
        HardshipReviewResultDTO response = hardshipService.calculateHardship(hardshipReviewCalculationDTO, FULL_THRESHOLD);
        assertThat(response.getDisposableIncomeAfterHardship()).isEqualTo(BigDecimal.valueOf(-5620.25));
        assertThat(response.getHardshipSummary()).isEqualTo(BigDecimal.valueOf(10620.25));
        assertThat(response.getHardshipReviewResult()).isEqualTo("PASS");
    }

    @Test
    void givenValidHardshipReviewCalculationDTOWithZeroAmount_whenCalculateHardshipIsInvoked_thenHardshipReviewResultDTOIsReturned() {
        HardshipReviewCalculationDTO hardshipReviewCalculationDTO = getHardshipReviewCalculationDTO(EXPENDITURE);
        hardshipReviewCalculationDTO.getHardshipReviewCalculationDetails()
                .forEach(hRCalcDetail -> hRCalcDetail.setAmount(BigDecimal.ZERO));
        HardshipReviewResultDTO response = hardshipService.calculateHardship(hardshipReviewCalculationDTO, FULL_THRESHOLD);
        assertThat(response.getDisposableIncomeAfterHardship()).isEqualTo(BigDecimal.valueOf(5000.0));
        assertThat(response.getHardshipSummary()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getHardshipReviewResult()).isEqualTo("FAIL");
    }

    @Test
    void givenValidHardshipReviewCalculationDTOWithAcceptedAsFalse_whenCalculateHardshipIsInvoked_thenHardshipReviewResultDTOIsReturned() {
        HardshipReviewCalculationDTO hardshipReviewCalculationDTO = getHardshipReviewCalculationDTO(EXPENDITURE);
        hardshipReviewCalculationDTO.getHardshipReviewCalculationDetails()
                .forEach(hRCalcDetail -> hRCalcDetail.setAccepted("N"));
        HardshipReviewResultDTO response = hardshipService.calculateHardship(hardshipReviewCalculationDTO, FULL_THRESHOLD);
        assertThat(response.getDisposableIncomeAfterHardship()).isEqualTo(BigDecimal.valueOf(5000.0));
        assertThat(response.getHardshipSummary()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getHardshipReviewResult()).isEqualTo("FAIL");
    }

    @Test
    void givenValidHardshipReviewCalculationDTOWithEmptyDetails_whenCalculateHardshipIsInvoked_thenHardshipReviewResultDTOIsReturned() {
        HardshipReviewCalculationDTO hardshipReviewCalculationDTO = getHardshipReviewCalculationDTO();
        HardshipReviewResultDTO response = hardshipService.calculateHardship(hardshipReviewCalculationDTO, FULL_THRESHOLD);
        assertThat(response.getDisposableIncomeAfterHardship()).isEqualTo(BigDecimal.valueOf(5000.0));
        assertThat(response.getHardshipSummary()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getHardshipReviewResult()).isEqualTo("FAIL");
    }

}