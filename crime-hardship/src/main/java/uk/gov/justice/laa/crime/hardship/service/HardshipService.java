package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDTO;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDetail;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewResultDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType.*;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult.FAIL;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewResult.PASS;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    private final MaatCourtDataService maatCourtDataService;

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(final ApiCalculateHardshipByDetailRequest request) {
        ApiCalculateHardshipByDetailResponse apiProcessRepOrderResponse = new ApiCalculateHardshipByDetailResponse();
        BigDecimal hardshipSummary = BigDecimal.ZERO;

        List<HardshipReviewDetail> hardshipReviewDetailList = maatCourtDataService.getHardshipByDetailType(
                        request.getRepId(), request.getDetailType(), request.getLaaTransactionId())
                .stream().filter(hrd -> "Y".equals(hrd.getAccepted()) && BigDecimal.ZERO.compareTo(hrd.getAmount()) != 0).toList();

        for (HardshipReviewDetail hardshipReviewDetail : hardshipReviewDetailList) {
            hardshipSummary = hardshipSummary.add(
                    hardshipReviewDetail.getAmount().multiply(
                            BigDecimal.valueOf(hardshipReviewDetail.getFrequency().getAnnualWeighting()))
            );
        }
        apiProcessRepOrderResponse.setHardshipSummary(hardshipSummary);
        return apiProcessRepOrderResponse;
    }

    public HardshipReviewResultDTO calculateHardship(final HardshipReviewCalculationDTO hardshipReviewCalculationDTO, final BigDecimal fullThreshold) {
        log.info("Calculating hardship for {}", hardshipReviewCalculationDTO);
        BigDecimal hardshipSummary = BigDecimal.ZERO;

        if (!isEmpty(hardshipReviewCalculationDTO.getHardshipReviewCalculationDetails())) {
            for (HardshipReviewCalculationDetail hRDetailDTO : hardshipReviewCalculationDTO.getHardshipReviewCalculationDetails()) {
                if (Arrays.asList(INCOME, SOL_COSTS, EXPENDITURE).contains(hRDetailDTO.getDetailType())
                        && BigDecimal.ZERO.compareTo(hRDetailDTO.getAmount()) != 0 && "Y".equals(hRDetailDTO.getAccepted())) {
                    hardshipSummary = hardshipSummary.add(hRDetailDTO.getAmount()
                            .multiply(BigDecimal.valueOf(hRDetailDTO.getFrequency().getAnnualWeighting())));
                }
            }
        }
        final BigDecimal disposableIncome = hardshipReviewCalculationDTO.getDisposableIncome();
        final BigDecimal disposableIncomeAfterHardship = disposableIncome.subtract(hardshipSummary);
        HardshipReviewResult reviewResult = FAIL;

        if (disposableIncomeAfterHardship.compareTo(fullThreshold) <= 0) {
            reviewResult = PASS;
        }
        return HardshipReviewResultDTO.builder()
                .hardshipSummary(hardshipSummary)
                .hardshipReviewResult(reviewResult.name())
                .disposableIncome(disposableIncome)
                .disposableIncomeAfterHardship(disposableIncomeAfterHardship)
                .build();
    }

}
