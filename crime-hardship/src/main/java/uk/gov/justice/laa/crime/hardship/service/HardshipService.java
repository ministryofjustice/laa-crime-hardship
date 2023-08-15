package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.exeption.ValidationException;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailRequest;
import uk.gov.justice.laa.crime.hardship.model.ApiCalculateHardshipByDetailResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReviewDetail;
import uk.gov.justice.laa.crime.hardship.model.SolicitorCosts;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.Frequency;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewDetailType;
import uk.gov.justice.laa.crime.hardship.validation.HardshipReviewValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    public static final String MSG_INCORRECT_ROLE = "User does not have correct role for this work reason";
    private final MaatCourtDataService maatCourtDataService;

    private Optional<Void> calculateSolicitorEstimatedTotalCost(HardshipReviewDTO hardshipReviewDTO) {


        BigDecimal solEstTotalCost = (hardshipReviewDTO.getSolicitorCosts().getSolicitorRate()
                .multiply(hardshipReviewDTO.getSolicitorCosts().getSolicitorHours()))
                .add(Optional.ofNullable(hardshipReviewDTO.getSolicitorCosts().getSolicitorVat()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(hardshipReviewDTO.getSolicitorCosts().getSolicitorDisb()).orElse(BigDecimal.ZERO))
                );

        hardshipReviewDTO.getSolicitorCosts().setSolicitorEstTotalCost(solEstTotalCost);

        HardshipReviewDetail hardshipReviewDetail = new HardshipReviewDetail();
        hardshipReviewDetail.setDetailType(HardshipReviewDetailType.SOL_COSTS);

        hardshipReviewDTO.getReviewDetails().add(hardshipReviewDetail);
        return Optional.empty();
    }

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

        HardshipReviewValidator.validateCompletedHardship(hardshipReviewDTO);

        checkNewWorkReasonAuthorisation(hardshipReviewDTO);

        HardshipReviewValidator.validateHardshipMandatoryFields(hardshipReviewDTO);

        calculateSolicitorEstimatedTotalCost(hardshipReviewDTO);


        if (hardshipReviewDTO.getReviewDetails() != null) {
            hardshipReviewDTO.getReviewDetails().forEach(hrDetailType -> {
                switch (hrDetailType.getDetailType().getType()) {
                    case "FUNDING" -> {
                        if (hrDetailType.getOtherDescription() != null) {
                            HardshipReviewValidator.validateHardshipReviewFundingItem(hrDetailType);
                            hrDetailType.setFrequency(Frequency.MONTHLY);
                        }
                    }
                    case "SOL COSTS" -> {
                        hrDetailType.setFrequency(Frequency.ANNUALLY);
                        hrDetailType.setAmount(hardshipReviewDTO.getSolicitorCosts().getSolicitorEstTotalCost());
                        hrDetailType.setAccepted("Y");
                    }
                    case "INCOME" -> HardshipReviewValidator.validateHardshipReviewIncomeItem(hrDetailType);
                    case "EXPENDITURE" -> HardshipReviewValidator.validateHardshipReviewExpenditureItem(hrDetailType);
                    default -> log.debug("Invalid case type"); // added for code smell
                }
            });
        }

        if (hardshipReviewDTO.getReviewProgressItems() != null) {
            hardshipReviewDTO.getReviewProgressItems().stream().forEach(HardshipReviewValidator::validateHardshipReviewProgressItem);
        }
        return hardshipReviewDTO;
    }

    private void checkNewWorkReasonAuthorisation(HardshipReviewDTO hardshipReviewDTO) {
        if (!maatCourtDataService.isNewWorkReasonAuthorized(hardshipReviewDTO.getNewWorkReason().userCreated(),
                hardshipReviewDTO.getNewWorkReason().code()).result()) {
            throw new ValidationException(MSG_INCORRECT_ROLE);
        }
    }


}



