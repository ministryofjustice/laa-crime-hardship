package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.stateless.StatelessApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.stateless.StatelessApiCalculateHardshipByDetailResponse;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {
    private final MaatCourtDataService maatCourtDataService;

    public StatelessApiCalculateHardshipByDetailResponse calculateHardshipForDetail(StatelessApiCalculateHardshipByDetailRequest request) {
        StatelessApiCalculateHardshipByDetailResponse apiProcessRepOrderResponse = new StatelessApiCalculateHardshipByDetailResponse();
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

}
