package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.hardship.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.common.model.hardship.HardshipReview;
import uk.gov.justice.laa.crime.common.model.hardship.SolicitorCosts;
import uk.gov.justice.laa.crime.common.model.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.enums.HardshipReviewResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.mapper.HardshipDetailMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static uk.gov.justice.laa.crime.enums.HardshipReviewResult.FAIL;
import static uk.gov.justice.laa.crime.enums.HardshipReviewResult.PASS;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipCalculationService {

    private final HardshipDetailMapper detailMapper;
    private final MaatCourtDataService maatCourtDataService;

    private static BigDecimal calculateDetails(HardshipReview hardship) {
        BigDecimal total = Stream.of(hardship.getDeniedIncome(), hardship.getExtraExpenditure())
                .flatMap(Collection::stream)
                .filter(item -> Boolean.TRUE.equals(item.getAccepted()))
                .map(item -> item.getAmount()
                        .multiply(BigDecimal.valueOf(item.getFrequency().getWeighting())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        CourtType courtType = hardship.getCourtType();
        SolicitorCosts solicitorCosts = hardship.getSolicitorCosts();
        if (solicitorCosts != null && courtType == CourtType.MAGISTRATE) {
            BigDecimal estimatedTotal;
            if (solicitorCosts.getEstimatedTotal() != null) {
                estimatedTotal = solicitorCosts.getEstimatedTotal();
            } else {
                estimatedTotal = solicitorCosts.getRate()
                        .multiply(solicitorCosts.getHours())
                        .add(solicitorCosts.getVat())
                        .add(solicitorCosts.getDisbursements());
                solicitorCosts.setEstimatedTotal(estimatedTotal);
            }
            total = total.add(estimatedTotal);
        }
        return total;
    }

    public ApiCalculateHardshipByDetailResponse calculateHardshipForDetail(Integer repId,
                                                                           HardshipReviewDetailType detailType) {
        List<ApiHardshipDetail> response =
                maatCourtDataService.getHardshipByDetailType(repId, detailType.getType());

        BigDecimal total = BigDecimal.ZERO;
        if (response != null) {
            HardshipReview hardship = new HardshipReview();
            detailMapper.toDto(response, hardship);
            total = calculateDetails(hardship);
        }
        return new ApiCalculateHardshipByDetailResponse()
                .withHardshipSummary(total);
    }

    public HardshipResult calculateHardship(final HardshipReview hardship, final BigDecimal fullThreshold) {

        BigDecimal total = calculateDetails(hardship);

        final BigDecimal disposableIncomeAfterHardship =
                hardship.getTotalAnnualDisposableIncome()
                        .subtract(total)
                        .setScale(2, RoundingMode.HALF_UP);

        HardshipReviewResult result = FAIL;
        if (disposableIncomeAfterHardship.compareTo(fullThreshold) <= 0) {
            result = PASS;
        }
        return HardshipResult.builder()
                .result(result)
                .resultDate(LocalDate.now())
                .postHardshipDisposableIncome(disposableIncomeAfterHardship)
                .build();
    }
}
