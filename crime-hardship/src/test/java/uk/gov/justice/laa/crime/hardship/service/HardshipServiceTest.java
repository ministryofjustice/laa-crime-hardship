package uk.gov.justice.laa.crime.hardship.service;

import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.exeption.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.*;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailCode;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class HardshipServiceTest {

    public static final String NEW_WORK_REASON_CODE = "Reason Code";

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private HardshipService hardshipService;

    @Test
    void givenHardshipReviewAmount_whenCalculateHardshipByDetailIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);
        List<HardshipReviewDetail> hardshipReviewDetailList = TestModelDataBuilder.getHardshipReviewDetailList("Y", 100);
        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);
        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void givenHardshipDetailWithZeroAmount_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);
        List<HardshipReviewDetail> hardshipReviewDetailList = TestModelDataBuilder.getHardshipReviewDetailList("Y", 0);
        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);
        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void givenHardshipDetailWithNotAccepted_whenCalculateEvidenceFeeIsInvoked_validResponseIsReturned() {
        ApiCalculateHardshipByDetailRequest request = TestModelDataBuilder.getApiCalculateHardshipByDetailRequest(true);
        List<HardshipReviewDetail> hardshipReviewDetailList = TestModelDataBuilder.getHardshipReviewDetailList("N", 10);
        when(maatCourtDataService.getHardshipByDetailType(anyInt(), anyString(), anyString()))
                .thenReturn(hardshipReviewDetailList);
        ApiCalculateHardshipByDetailResponse response = hardshipService.calculateHardshipForDetail(request);

        assertThat(response.getHardshipSummary())
                .isEqualTo(BigDecimal.ZERO);
    }


    @Test
    void givenValidHardshipReviewDTO_whenCheckHardshipIsInvoked_validResponseIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE, LocalDateTime.now(),
                SolicitorCosts.builder().solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        HardshipReviewDTO response = hardshipService.checkHardship(hardshipReviewDTO);
        assertThat(response).isNotNull();
    }


    @Test
    void givenValidHardshipReviewDTO_whenCheckNewWorkReasonAuthorisationIsInvoked_validResponseIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),SolicitorCosts.builder().solicitorVat(BigDecimal.valueOf(20))
                        .solicitorDisb(BigDecimal.valueOf(20)).solicitorRate(BigDecimal.valueOf(10))
                        .solicitorHours(BigDecimal.valueOf(100)).build());

        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        assertThat(hardshipService.checkHardship(hardshipReviewDTO)).isNotNull();
    }


    @Test
    void givenValidHardshipReviewDTO_whenCheckHardshipIsInvokedAndAuthorisationIsFalse_ExceptionIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                null);
        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(false));
        assertThatThrownBy(() -> hardshipService.checkHardship(hardshipReviewDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(HardshipService.MSG_INCORRECT_ROLE);
    }

    @Test
    void givenValidHardshipReviewDTOWithDetailTypeFunding_whenCheckHardshipIsInvokedAndAuthorisationIsTrue_validResponseIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),SolicitorCosts.builder().solicitorVat(BigDecimal.valueOf(20))
                        .solicitorDisb(BigDecimal.valueOf(20)).solicitorRate(BigDecimal.valueOf(10))
                        .solicitorHours(BigDecimal.valueOf(100)).build());

        hardshipReviewDTO.getReviewDetails().get(0).setDetailType(HardshipReviewDetailType.FUNDING);
        hardshipReviewDTO.getReviewDetails().get(0).setOtherDescription("DESCRIPTION");
        hardshipReviewDTO.getReviewDetails().get(0).setAmount(BigDecimal.valueOf(10.0));
        hardshipReviewDTO.getReviewDetails().get(0).setDateDue(LocalDateTime.now());

        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        hardshipReviewDTO = hardshipService.checkHardship(hardshipReviewDTO);
        assertThat(hardshipReviewDTO.getReviewDetails().get(0).getFrequency()).isEqualTo(Frequency.MONTHLY);
    }


    @Test
    void givenValidHardshipReviewDTOWithDetailTypeSolCosts_whenCheckHardshipIsInvokedAndAuthorisationIsTrue_validResponseIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                null);
        hardshipReviewDTO.getReviewDetails().get(0).setDetailType(HardshipReviewDetailType.SOL_COSTS);
        hardshipReviewDTO.setSolicitorCosts(SolicitorCosts.builder()
                .solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());

        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        hardshipReviewDTO = hardshipService.checkHardship(hardshipReviewDTO);
        assertThat(hardshipReviewDTO.getReviewDetails().get(0).getFrequency()).isEqualTo(Frequency.ANNUALLY);
    }

    @Test
    void givenValidSolicitorCosts_whenCheckHardshipIsInvokedAndAuthorisationIsTrue_validSolCostIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                null);
        hardshipReviewDTO.getReviewDetails().get(0).setDetailType(HardshipReviewDetailType.SOL_COSTS);
        hardshipReviewDTO.setSolicitorCosts(SolicitorCosts.builder().solicitorVat(BigDecimal.valueOf(20))
                 .solicitorDisb(BigDecimal.valueOf(20)).solicitorRate(BigDecimal.valueOf(10))
                 .solicitorHours(BigDecimal.valueOf(100)).build());

        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        hardshipReviewDTO = hardshipService.checkHardship(hardshipReviewDTO);
        assertThat(hardshipReviewDTO.getSolicitorCosts().getSolicitorEstTotalCost().intValue()).isEqualTo(1040);
    }

    @Test
    void givenValidHardshipReviewDTOWithDetailTypeIncome_whenCheckHardshipIsInvokedAndAuthorisationIsTrue_validResponseIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                null);
        hardshipReviewDTO.getReviewDetails().get(0).setDetailType(HardshipReviewDetailType.INCOME);
        hardshipReviewDTO.setSolicitorCosts(SolicitorCosts.builder()
                .solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.getReviewDetails().get(0).setDescription("Description");
        hardshipReviewDTO.getReviewDetails().get(0).setAmount(BigDecimal.valueOf(100));
        hardshipReviewDTO.getReviewDetails().get(0).setFrequency(Frequency.MONTHLY);
        hardshipReviewDTO.getReviewDetails().get(0).setDetailCode(HardshipReviewDetailCode.BAILIFF);
        hardshipReviewDTO.getReviewDetails().get(0).setReasonNote("Reason Note");

        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        hardshipReviewDTO = hardshipService.checkHardship(hardshipReviewDTO);
        assertThat(hardshipService.checkHardship(hardshipReviewDTO)).isNotNull();
    }


    @Test
    void givenValidHardshipReviewDTOWithDetailTypeExpenditure_whenCheckHardshipIsInvokedAndAuthorisationIsTrue_validResponseIsReturned() {
        HardshipReviewDTO hardshipReviewDTO = TestModelDataBuilder.buildHardshipReviewDTO(NEW_WORK_REASON_CODE,
                LocalDateTime.now(),
                null);
        hardshipReviewDTO.getReviewDetails().get(0).setDetailType(HardshipReviewDetailType.EXPENDITURE);
        hardshipReviewDTO.setSolicitorCosts(SolicitorCosts.builder()
                .solicitorRate(BigDecimal.valueOf(10)).solicitorHours(BigDecimal.valueOf(100)).build());
        hardshipReviewDTO.getReviewDetails().get(0).setDescription("Description");
        hardshipReviewDTO.getReviewDetails().get(0).setAmount(BigDecimal.valueOf(100));
        hardshipReviewDTO.getReviewDetails().get(0).setFrequency(Frequency.MONTHLY);
        hardshipReviewDTO.getReviewDetails().get(0).setDetailCode(HardshipReviewDetailCode.BAILIFF);
        hardshipReviewDTO.getReviewDetails().get(0).setReasonNote("Reason Note");
        hardshipReviewDTO.getReviewDetails().get(0).setDetailReason(TestModelDataBuilder.buildHardshipReviewDetailReason());

        when(maatCourtDataService.isNewWorkReasonAuthorized(anyString(), anyString()))
                .thenReturn(new AuthorizationResponse(true));
        hardshipReviewDTO = hardshipService.checkHardship(hardshipReviewDTO);
        assertThat(hardshipService.checkHardship(hardshipReviewDTO)).isNotNull();
    }
}