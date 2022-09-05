package sade.opm.rest.model;

public class UserLogin {
    public final String username;
    public final String password;

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
