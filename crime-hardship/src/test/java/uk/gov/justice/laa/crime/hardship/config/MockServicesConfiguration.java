package uk.gov.justice.laa.crime.hardship.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);
        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();

        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();
        ServicesConfiguration.CmaApi cmaApiConfiguration = new ServicesConfiguration.CmaApi();

        ServicesConfiguration.MaatApi.HardshipEndpoints hardshipEndpoints =
                new ServicesConfiguration.MaatApi.HardshipEndpoints(
                        "/hardship/repId/{repId}/detailType/{detailType}",
                        "/hardship",
                        "/hardship/{hardshipReviewId}"
                );
        ServicesConfiguration.CmaApi.CmaEndpoints cmaEndpoints =
                new ServicesConfiguration.CmaApi.CmaEndpoints(
                        "/fullAssessmentThreshold/{assessmentDate}"
                );

        maatApiConfiguration.setBaseUrl(host);
        maatApiConfiguration.setHardshipEndpoints(hardshipEndpoints);

        cmaApiConfiguration.setBaseUrl(host);
        cmaApiConfiguration.setCmaEndpoints(cmaEndpoints);

        servicesConfiguration.setMaatApi(maatApiConfiguration);
        servicesConfiguration.setCmaApi(cmaApiConfiguration);

        return servicesConfiguration;
    }
}
