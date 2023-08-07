package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.exeption.ValidationException;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;

import java.math.BigDecimal;

import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;

import java.util.List;

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

    public HardshipReviewDTO checkHardship(HardshipReviewDTO hardshipReviewDTO) {
        BigDecimal solEstTotalCost = null;

        if (hardshipReviewDTO.reviewStatus().getStatus() != null &&
                hardshipReviewDTO.reviewStatus().getStatus().equalsIgnoreCase(HardshipReviewStatus.COMPLETE.getStatus())) {
            if (hardshipReviewDTO.reviewDate() == null)
                throw new ValidationException("The Review Date must be entered for completed hardship");
        }

        if (hardshipReviewDTO.newWorkReason().getCode() == null) {
            throw new ValidationException("The Review Reason must be entered for hardship");
        }

        if (hardshipReviewDTO.reviewDetails() != null && hardshipReviewDTO.reviewDetails().size() > 0) {

        }

        if (hardshipReviewDTO.solicitorCosts() != null && hardshipReviewDTO.solicitorCosts().getSolicitorRate() != null) {
            if (hardshipReviewDTO.solicitorCosts().getSolicitorHours() == null) {
                throw new ValidationException("Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified");
            }

            if (hardshipReviewDTO.solicitorCosts().getSolicitorVat() != null &&
                    hardshipReviewDTO.solicitorCosts().getSolicitorHours() != null &&
                    hardshipReviewDTO.solicitorCosts().getSolicitorVat() != null &&
                    hardshipReviewDTO.solicitorCosts().getSolicitorDisb() != null
            ) {
                solEstTotalCost = (hardshipReviewDTO.solicitorCosts().getSolicitorRate()
                        .multiply(hardshipReviewDTO.solicitorCosts().getSolicitorHours()))
                        .add(hardshipReviewDTO.solicitorCosts().getSolicitorVat().add(hardshipReviewDTO.solicitorCosts().getSolicitorDisb())
                        );
            }

            hardshipReviewDTO.solicitorCosts().setSolicitorEstTotalCost(solEstTotalCost);

            HardshipReviewDetail hardshipReviewDetail = new HardshipReviewDetail();
            hardshipReviewDetail.setDetailType(HardshipReviewDetailType.SOL_COSTS);
            hardshipReviewDTO.reviewDetails().add(hardshipReviewDetail);

        }


        if (hardshipReviewDTO.reviewDetails() != null) {
            hardshipReviewDTO.reviewDetails().stream().forEach(hrDetailType -> {
                switch (hrDetailType.getDetailType().getType()) {
                    case "FUNDING" -> {
                        if (hrDetailType.getOtherDescription() != null) {
                            if (hrDetailType.getAmount() == null || hrDetailType.getDateDue() == null) {
                                throw new ValidationException("Amount and Date Expected must be entered for each detail in section " + hrDetailType.getDescription());
                            }
                            hrDetailType.setFrequency(Frequency.MONTHLY);
                        }
                    }
                    case "SOL COSTS" -> {
                        hrDetailType.setFrequency(Frequency.ANNUALLY);
                        hrDetailType.setAmount(hardshipReviewDTO.solicitorCosts().getSolicitorEstTotalCost());
                        hrDetailType.setAccepted("Y");
                    }
                    case "INCOME" -> {
                        if (hrDetailType.getDetailCode().getDescription() != null) {
                            if (hrDetailType.getAmount() == null
                                    || hrDetailType.getFrequency().getCode() == null
                                    || hrDetailType.getReasonNote() == null) {
                                throw new ValidationException("Amount, Frequency, and Reason must be entered for each detail in section " + hrDetailType.getDescription());
                            }
                        }
                    }
                    case "EXPENDITURE" -> {
                        if (hrDetailType.getDetailCode().getDescription() != null) {
                            if (hrDetailType.getAmount() == null
                                    || hrDetailType.getFrequency().getCode() == null
                                    || hrDetailType.getDetailReason().getId() == null) {
                                throw new ValidationException("Amount, Frequency, and Reason must be entered for each detail in section " + hrDetailType.getDescription());
                            }
                        }
                    }
                }
            });

        }

        return null;

    }
}



