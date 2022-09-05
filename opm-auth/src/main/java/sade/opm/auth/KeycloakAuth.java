package sade.opm.auth;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.FinalizablePhantomReference;
import org.keycloak.admin.client.*;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;


public class KeycloakAuth implements UserAuth {

    private Keycloak keycloak;
    private KeycloakConfig keycloakConfig;

    public KeycloakAuth(KeycloakConfig keycloakConfig){
        this.keycloakConfig = keycloakConfig;
        connect();
    }

    public void connect(){
        keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakConfig.getServerUrl())
                .realm(keycloakConfig.getRealm())
                .username(keycloakConfig.getUsername())
                .password(keycloakConfig.getPassword())
                .clientId(keycloakConfig.getClientId())
                .build();
        String token = keycloak.tokenManager().getAccessTokenString();

    }

    public String login(final String username , final String password){
        keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakConfig.getServerUrl())
                .realm(keycloakConfig.getRealm())
                .username(username)
                .password(password)
                .clientId(keycloakConfig.getClientId())
                .build();
        return keycloak.tokenManager().getAccessTokenString();
    }

    public String getUserIdByName(final String username){
        final UserRepresentation userRepresentation =
        keycloak.realm(keycloakConfig.getRealm()).users().list().stream()
                .filter(userRepresentationTmp -> userRepresentationTmp.getUsername().equals(username))
                .findAny()
                .orElse(null);
        return userRepresentation.getId();
    }

    public String getClientId(){
        final ClientRepresentation clientRepresentation =
        keycloak.realm(keycloakConfig.getRealm()).clients().findAll()
                .stream()
                .filter(clientRepresentationTmp -> clientRepresentationTmp.getClientId().equals(keycloakConfig.getClientId()))
                .findAny()
                .orElse(null);
        return clientRepresentation.getId();
    }

    public List<GroupRepresentation> getGroupsForUser(final String username){
        final String userId = getUserIdByName(username);
        return keycloak.realm(keycloakConfig.getRealm()).users().get(userId).groups();
    }

    public List<RoleRepresentation> getRolesForUser(final String username){
        final String userId = getUserIdByName(username);
        return keycloak.realm(keycloakConfig.getRealm()).users().get(userId).roles()
                .clientLevel(getClientId()).listEffective();
    }

    public String getGroupsForUserStr(final String username){
        return getGroupsForUser(username).stream().map(e -> e.getName().toString()).collect(Collectors.joining(","));
    }

    public String getRolesForUserStr(final String username){
        return getRolesForUser(username).stream().map(e -> e.getName().toString()).collect(Collectors.joining(","));
    }

    public boolean isTokenValid(String token){
        try {
            final DecodedJWT jwt = JWT.decode(token);
            final JwkProvider provider = new UrlJwkProvider(keycloakConfig.getCertUrl());
            final Jwk jwk = provider.get(jwt.getKeyId());
            final Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            algorithm.verify(jwt);
            return true;
        } catch (JwkException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token){
        final DecodedJWT jwt = JWT.decode(token);
        if(jwt.getExpiresAt().before(new Date())){
            return true;
        }else {
            return false;
        }
    }
/*
    public String groups(){
        keycloak = Keycloak.getInstance(keycloakConfig.getServerUrl(),keycloakConfig.getRealm() ,keycloakConfig.getClientId(),token );
        String groups = keycloak.realm(keycloakConfig.getRealm()).groups().groups().stream().map(e -> e.toString()).reduce("", String::concat);
        return groups;
    }
*/

}
