package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.PersistHardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    private final PersistHardshipMapper mapper;
    private final MaatCourtDataService maatCourtDataService;
    private final HardshipCalculationService hardshipCalculationService;

    public HardshipReviewDTO create(HardshipReviewDTO hardshipReviewDTO, String laaTransactionId) {
        return persist(hardshipReviewDTO, laaTransactionId, RequestType.CREATE);
    }

    public HardshipReviewDTO update(HardshipReviewDTO hardshipReviewDTO, String laaTransactionId) {
        return persist(hardshipReviewDTO, laaTransactionId, RequestType.UPDATE);
    }

    public HardshipReviewDTO rollback(HardshipReviewDTO hardshipReviewDTO, String laaTransactionId) {

        hardshipReviewDTO.getHardshipMetadata().setReviewStatus(HardshipReviewStatus.IN_PROGRESS);

        ofNullable(hardshipReviewDTO.getHardshipResult())
                .orElse(new HardshipResult())
                .setResult(null);

        ApiPersistHardshipRequest request = mapper.fromDto(hardshipReviewDTO);
        ApiPersistHardshipResponse response =
                maatCourtDataService.persistHardship(request, laaTransactionId, RequestType.UPDATE);
        mapper.toDto(response, hardshipReviewDTO);
        return hardshipReviewDTO;
    }

    private HardshipReviewDTO persist(HardshipReviewDTO hardshipReviewDTO, String laaTransactionId, RequestType requestType) {
        HardshipReview hardship = hardshipReviewDTO.getHardship();
        // TODO: Full threshold should be retrieved from CMA (LCAM-960)
        HardshipResult result = hardshipCalculationService.calculateHardship(hardship, BigDecimal.valueOf(3398.00));
        hardshipReviewDTO.setHardshipResult(result);
        ApiPersistHardshipRequest request = mapper.fromDto(hardshipReviewDTO);
        ApiPersistHardshipResponse response =
                maatCourtDataService.persistHardship(request, laaTransactionId, requestType);
        mapper.toDto(response, hardshipReviewDTO);
        // Call Contribution service and CCP from Orchestration layer
        return hardshipReviewDTO;
    }
}
