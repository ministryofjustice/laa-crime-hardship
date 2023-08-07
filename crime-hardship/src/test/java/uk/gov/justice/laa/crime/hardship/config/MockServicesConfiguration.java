package uk.gov.justice.laa.crime.hardship.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);

        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();

        ServicesConfiguration.MaatApi.HardshipEndpoints hardshipEndpoints =
                new ServicesConfiguration.MaatApi.HardshipEndpoints(
                        "/hardship/repId/{repId}/detailType/{detailType}"
                );

        maatApiConfiguration.setBaseUrl(host);
        maatApiConfiguration.setHardshipEndpoints(hardshipEndpoints);

        servicesConfiguration.setOAuthEnabled(false);
        servicesConfiguration.setMaatApi(maatApiConfiguration);

        return servicesConfiguration;
    }
}
