package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewCalculationDetail;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDetailDTO;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewResultDTO;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;
import static uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    private final MaatCourtDataService maatCourtDataService;

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(ApiCalculateHardshipByDetailRequest request) {
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

    public HardshipReviewResultDTO calculateHardship(final HardshipReviewCalculationDetail hardshipReviewCalculationDetail, final BigDecimal fullThreshold) {
        log.info("Calculating hardship for {}", hardshipReviewCalculationDetail);
        BigDecimal hardshipSummary = BigDecimal.ZERO;

        if (!isEmpty(hardshipReviewCalculationDetail.getHardshipReviewDetailDTOS())) {
            for (HardshipReviewDetailDTO hRDetailDTO : hardshipReviewCalculationDetail.getHardshipReviewDetailDTOS()) {
                if (Arrays.asList(INCOME, SOL_COSTS, EXPENDITURE).contains(hRDetailDTO.getDetailType())) {
                    if (BigDecimal.ZERO.compareTo(hRDetailDTO.getAmount()) != 0 && "Y".equals(hRDetailDTO.getAccepted())) {
                        hardshipSummary = hardshipSummary.add(hRDetailDTO.getAmount()
                                .multiply(BigDecimal.valueOf(hRDetailDTO.getFrequency().getAnnualWeighting())));
                    }
                }
            }
        }
        final BigDecimal disposableIncome = hardshipReviewCalculationDetail.getDisposableIncome();
        final BigDecimal postHardshipDisposableIncome = disposableIncome.subtract(hardshipSummary);
        String reviewResult = "FAIL";

        if (postHardshipDisposableIncome.compareTo(fullThreshold) <= 0) {
            reviewResult = "PASS";
        }
        return HardshipReviewResultDTO.builder()
                .hardshipSummary(hardshipSummary)
                .hardshipReviewResult(reviewResult)
                .disposableIncome(disposableIncome)
                .postHardshipDisposableIncome(postHardshipDisposableIncome)
                .build();
    }

}
