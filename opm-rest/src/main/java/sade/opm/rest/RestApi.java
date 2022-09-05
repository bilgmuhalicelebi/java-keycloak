package sade.opm.rest;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;

import sade.opm.auth.UserAuth;
import sade.opm.rest.controller.UserAuthController;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.*;

public class RestApi {

    private UserAuthController userAuthController;
    private UserAuth userAuth;

    public RestApi(int port , UserAuth userAuth){
        this.userAuth = userAuth;
        userAuthController = new UserAuthController(userAuth);
        start(port);
    }

    public void start(int port){
        Javalin app = Javalin.create(config -> {
            config.registerPlugin(getConfiguredOpenApiPlugin());
            config.defaultContentType = "application/json";
            config.enableCorsForAllOrigins();
        }).before(ctx -> {
            if(!ctx.path().equals("/user-auth/login")){
                userAuthController.checkToken(ctx);
            }
        }).routes(() -> {
            path("user-auth", () -> {
                get(ctx -> ctx.result("Hello World"));
                path("login", () -> {
                    post(ctx -> {userAuthController.login(ctx);});
                });
                path("check-token", () -> {
                    post(ctx -> {userAuthController.checkToken(ctx);});
                });
            });
        }).start(port);
    }

    private OpenApiPlugin getConfiguredOpenApiPlugin() {
        Info info = new Info().title("Sade OPM").version("1.0").description("Sade OPM Rest API");
        OpenApiOptions options = new OpenApiOptions(info)
                .path("/swagger-docs") // endpoint for OpenAPI json
                .swagger(new SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
                .reDoc(new ReDocOptions("/redoc")) // endpoint for redoc
                .defaultDocumentation(doc -> {
                    //doc.json("500", ErrorResponse.class);
                    //doc.json("503", ErrorResponse.class);
                });
        return new OpenApiPlugin(options);
    }
}
