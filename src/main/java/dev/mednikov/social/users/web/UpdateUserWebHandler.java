package dev.mednikov.social.users.web;

import dev.mednikov.social.users.data.UserRepository;
import dev.mednikov.social.users.exceptions.UserNotInitializedException;
import dev.mednikov.social.users.mappers.CurrentUserJsonMapper;
import dev.mednikov.social.users.mappers.UserJsonMapper;
import dev.mednikov.social.users.models.UserGender;
import dev.mednikov.social.users.models.UserRelationshipStatus;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public final class UpdateUserWebHandler implements Handler<RoutingContext> {

    private final UserRepository userRepository;

    public UpdateUserWebHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(RoutingContext context) {
        JsonObject principal = context.user().principal();
        JsonObject body = context.body().asJsonObject();
        System.out.println(body.encode());

        UUID authId = UUID.fromString(principal.getString("sub"));
        this.userRepository.findByAuthId(authId).compose(result -> {
            if (result.isPresent()){
                return Future.succeededFuture(result.get());
            } else {
               return Future.failedFuture(new UserNotInitializedException());
            }
        }).compose(result -> {
            result.setGender(UserGender.valueOf(body.getString("gender").toUpperCase()));
            result.setRelationshipStatus(UserRelationshipStatus.valueOf(
                    body.getString("relationshipStatus").toUpperCase()
            ));
            result.setSummary(body.getString("summary"));
            return this.userRepository.updateUser(result);
        }).onComplete(result -> {
            if (result.succeeded()){
                UserJsonMapper mapper = new CurrentUserJsonMapper();
                JsonObject payload = mapper.apply(result.result());
                context.response().setStatusCode(200).end(payload.encode());
            } else {
                context.fail(result.cause());
            }
        });
    }
}
