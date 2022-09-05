package sade.opm.rest;

import sade.opm.auth.KeycloakAuth;
import sade.opm.auth.KeycloakConfig;
import sade.opm.auth.UserAuth;

public class MainApplication {
    public static void main(String[] args) throws Exception {
        KeycloakConfig keycloakConfig = new KeycloakConfig("keycloak.properties");
        UserAuth userAuth = new KeycloakAuth(keycloakConfig);
        userAuth.login("alicelebi","1234");
        RestApi restApi = new RestApi(8001 , userAuth);

    }
}
