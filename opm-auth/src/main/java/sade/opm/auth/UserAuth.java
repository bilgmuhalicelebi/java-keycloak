package sade.opm.auth;

public interface UserAuth {
    void connect();
    String login(final String username, final String password);
    boolean isTokenValid(String token);
    boolean isTokenExpired(String token);
    String getGroupsForUserStr(final String username);
    String getRolesForUserStr(final String username);
}
