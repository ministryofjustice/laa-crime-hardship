package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.PersistHardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.hardship.staticdata.enums.RequestType;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    private final PersistHardshipMapper mapper;
    private final MaatCourtDataService maatCourtDataService;
    private final HardshipCalculationService hardshipCalculationService;
    private final CrimeMeansAssessmentService crimeMeansAssessmentService;

    public HardshipReviewDTO create(HardshipReviewDTO hardshipReviewDTO) {
        return persist(hardshipReviewDTO, RequestType.CREATE);
    }

    public HardshipReviewDTO update(HardshipReviewDTO hardshipReviewDTO) {
        return persist(hardshipReviewDTO, RequestType.UPDATE);
    }

    public ApiFindHardshipResponse find(Integer hardshipId) {
        return maatCourtDataService.getHardship(hardshipId);
    }

    public HardshipReviewDTO rollback(HardshipReviewDTO hardshipReviewDTO) {
        hardshipReviewDTO.getHardshipMetadata().setReviewStatus(HardshipReviewStatus.IN_PROGRESS);
        if (hardshipReviewDTO.getHardshipResult() != null) {
            hardshipReviewDTO.getHardshipResult().setResult(null);
        }
        ApiPersistHardshipRequest request = mapper.fromDto(hardshipReviewDTO);
        ApiPersistHardshipResponse response =
                maatCourtDataService.persistHardship(request, RequestType.UPDATE);
        mapper.toDto(response, hardshipReviewDTO);
        return hardshipReviewDTO;
    }

    private HardshipReviewDTO persist(HardshipReviewDTO hardshipReviewDTO, RequestType requestType) {
        HardshipReview hardship = hardshipReviewDTO.getHardship();
        BigDecimal fullThreshold = crimeMeansAssessmentService
                .getFullAssessmentThreshold(hardship.getReviewDate());
        HardshipResult result = hardshipCalculationService.calculateHardship(hardship, fullThreshold);
        hardshipReviewDTO.setHardshipResult(result);
        ApiPersistHardshipRequest request = mapper.fromDto(hardshipReviewDTO);
        ApiPersistHardshipResponse response =
                maatCourtDataService.persistHardship(request, requestType);
        mapper.toDto(response, hardshipReviewDTO);
        return hardshipReviewDTO;
    }
}
