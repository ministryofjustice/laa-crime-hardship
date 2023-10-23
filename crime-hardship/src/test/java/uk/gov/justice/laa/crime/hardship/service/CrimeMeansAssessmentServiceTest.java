package uk.gov.justice.laa.crime.hardship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.hardship.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration;
import uk.gov.justice.laa.crime.hardship.data.builder.TestModelDataBuilder;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrimeMeansAssessmentServiceTest {

    @Mock
    private RestAPIClient cmaApiClient;

    @InjectMocks
    private CrimeMeansAssessmentService crimeMeansAssessmentService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenAValidAssessmentDate_whenGetFullAssessmentThresholdIsInvoked_thenResponseIsReturned() {
        when(cmaApiClient.get(any(), anyString(), anyString()))
                .thenReturn(BigDecimal.TEN);
        crimeMeansAssessmentService.getFullAssessmentThreshold(
                TestModelDataBuilder.ASSESSMENT_DATE);
        verify(cmaApiClient, atLeastOnce()).get(any(), anyString(), anyString());
    }
}