package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.hardship.client.MeansAssessmentApiClient;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrimeMeansAssessmentServiceTest {
    @Mock
    private MeansAssessmentApiClient cmaApiClient;

    @InjectMocks
    private CrimeMeansAssessmentService crimeMeansAssessmentService;

    @Test
    void givenAValidAssessmentDate_whenGetFullAssessmentThresholdIsInvoked_thenResponseIsReturned() {
        when(cmaApiClient.find(anyString()))
                .thenReturn(BigDecimal.TEN);
        crimeMeansAssessmentService.getFullAssessmentThreshold(
                TestModelDataBuilder.ASSESSMENT_DATE);
        verify(cmaApiClient, times(1)).find(anyString());
    }

}