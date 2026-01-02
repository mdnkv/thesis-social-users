package dev.mednikov.social.users.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WebVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(WebVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        OAuth2Options oAuth2Options = new OAuth2Options();
        oAuth2Options.setClientId(config().getString("APP_KEYCLOAK_CLIENT"));
        oAuth2Options.setClientSecret(config().getString("APP_KEYCLOAK_SECRET"));
        oAuth2Options.setSite(config().getString("APP_KEYCLOAK_URL"));
        oAuth2Options.setTenant(config().getString("APP_KEYCLOAK_REALM"));
        oAuth2Options.setValidateIssuer(false);
        KeycloakAuth.discover(vertx, oAuth2Options).compose(authProvider -> {
            logger.info("Keycloak auth is initialized");
            HttpServer server = vertx.createHttpServer();
            Router router = Router.router(vertx);
            router.route("/users").handler(OAuth2AuthHandler.create(vertx, authProvider));
            server.requestHandler(router);
            return server.listen(config().getInteger("APP_HTTP_PORT", 8000));
        }).onComplete(result -> {
            if (result.succeeded()) {
                logger.info("Web server is started on port {}", result.result().actualPort());
                startPromise.complete();
            } else {
                logger.error("Failed to start web server", result.cause());
                startPromise.fail(result.cause());
            }
        });
    }

}
