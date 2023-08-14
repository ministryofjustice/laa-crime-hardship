package uk.gov.justice.laa.crime.hardship.config;

import uk.gov.justice.laa.crime.hardship.config.ServicesConfiguration.MaatApi.HardshipEndpoints;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);

        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();

        HardshipEndpoints hardshipEndpoints = new HardshipEndpoints();
        hardshipEndpoints.setHardshipDetailUrl("/hardship/repId/{repId}/detailType/{detailType}");
        hardshipEndpoints.setNwrAuthUrl("/users/{username}/work-reasons/{nworCode}");

        maatApiConfiguration.setBaseUrl(host);
        maatApiConfiguration.setHardshipEndpoints(hardshipEndpoints);

        servicesConfiguration.setOAuthEnabled(false);
        servicesConfiguration.setMaatApi(maatApiConfiguration);

        return servicesConfiguration;
    }
}
