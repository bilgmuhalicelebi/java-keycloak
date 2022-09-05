package sade.opm.rest.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.core.util.Header;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import sade.opm.auth.UserAuth;
import sade.opm.rest.model.UserLogin;

import java.util.Locale;

public class UserAuthController {

    private UserAuth userAuth;

    public UserAuthController(UserAuth userAuth){
        this.userAuth = userAuth;
    }

    public void login(Context ctx) {
        try {
            ctx.header(Header.AUTHORIZATION);
            final Gson gson = new Gson();
            final UserLogin userLogin = gson.fromJson(ctx.body(), UserLogin.class);
            final String accessToken = userAuth.login(userLogin.username , userLogin.password);
            final String userRoles = userAuth.getRolesForUserStr(userLogin.username);
            final JsonObject mainObject = new JsonObject();
            mainObject.addProperty("accessToken",accessToken);
            mainObject.addProperty("username",userLogin.username);
            mainObject.addProperty("userRoles",userRoles);
            ctx.json(mainObject.toString());
            ctx.status(200);
        }catch (Exception e){
            ctx.status(500);
        }
    }

    public void checkToken(Context ctx) {
        try {
            final JsonObject mainObject = new JsonObject();
            final String token = (String) ctx.header(Header.AUTHORIZATION);
            if (token != null) {
                if(!userAuth.isTokenExpired(token) && userAuth.isTokenValid(token)){
                    mainObject.addProperty("isTokenValid",true);
                    ctx.json(mainObject.toString());
                }else{
                    mainObject.addProperty("isTokenValid",false);
                    ctx.json(mainObject.toString());
                }
            }
            ctx.status(200);
        }catch (Exception e){
            System.out.println(e);
            ctx.status(500);
        }
    }
}
