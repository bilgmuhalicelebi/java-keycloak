package sade.opm.auth;

import java.io.InputStream;
import java.util.Properties;

public class KeycloakConfig {

    private final String serverUrl;
    private final String realm;
    private final String username;
    private final String password;
    private final String clientId;
    private final String certUrl;


    public KeycloakConfig(String resourceName){
        Properties props = loadProperties(resourceName);
        this.serverUrl = props.getProperty("keycloak.serverUrl");
        this.realm = props.getProperty("keycloak.realm");
        this.username = props.getProperty("keycloak.username");
        this.password = props.getProperty("keycloak.password");
        this.clientId = props.getProperty("keycloak.clientId");
        this.certUrl = props.getProperty("keycloak.certUrl");
    }

    public static Properties loadProperties(final String resourceName){
        Properties props = new Properties();
        InputStream in = KeycloakConfig.class.getClassLoader().getResourceAsStream(resourceName);
        try {
            props.load(in);
        } catch (Exception e) {
            System.out.println("//TBD");
        }
        return props;
    }

    @Override
    public String toString() {
        return "KeycloakConfig{" +
                "serverUrl='" + serverUrl + '\'' +
                ", realm='" + realm + '\'' +
                ", username='" + username + '\'' +
                ", password='" + "******" + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getCertUrl() {
        return certUrl;
    }

}
