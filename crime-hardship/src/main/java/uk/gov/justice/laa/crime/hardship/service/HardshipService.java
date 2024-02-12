package uk.gov.justice.laa.crime.hardship.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.hardship.dto.HardshipResult;
import uk.gov.justice.laa.crime.hardship.dto.HardshipReviewDTO;
import uk.gov.justice.laa.crime.hardship.mapper.PersistHardshipMapper;
import uk.gov.justice.laa.crime.hardship.model.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.hardship.model.HardshipReview;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipRequest;
import uk.gov.justice.laa.crime.hardship.model.maat_api.ApiPersistHardshipResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    public void rollback(Integer hardshipReviewId) {
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("status", HardshipReviewStatus.IN_PROGRESS);
        updateFields.put("reviewResult", null);
        maatCourtDataService.patchHardship(hardshipReviewId, updateFields);
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
