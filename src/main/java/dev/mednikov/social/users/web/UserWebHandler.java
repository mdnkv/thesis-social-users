package dev.mednikov.social.users.web;

import dev.mednikov.social.users.data.UserRepository;
import dev.mednikov.social.users.exceptions.UserNotInitializedException;
import dev.mednikov.social.users.mappers.UserJsonMapper;
import dev.mednikov.social.users.models.User;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

final class UserWebHandler implements Handler<RoutingContext> {

    private final static Logger logger = LoggerFactory.getLogger(UserWebHandler.class);

    private final UserRepository userRepository;

    public UserWebHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(RoutingContext context) {
        Long userId = Long.parseLong(context.pathParam("userId"));
        JsonObject principal = context.user().principal();
        UUID authId = UUID.fromString(principal.getString("sub"));
        this.userRepository.findByAuthId(authId).compose(result -> {
            if (result.isPresent()){
                return Future.succeededFuture(result.get());
            } else {
                return Future.failedFuture(new UserNotInitializedException());
            }
        }).compose(current -> {
            boolean isCurrent = current.getId().equals(userId);
            return Future.all(
                    Future.succeededFuture(isCurrent),
                    userRepository.findById(userId)
            );
        }).onComplete(result -> {
            if (result.succeeded()) {
                boolean isCurrent = result.result().resultAt(0);
                Optional<User> userResult = result.result().resultAt(1);
                if (userResult.isPresent()) {
                    User user = userResult.get();
                    UserJsonMapper mapper = new UserJsonMapper();
                    JsonObject payload = mapper.apply(user);
                    payload.put("currentUser", isCurrent);
                    context.response().setStatusCode(200).end(payload.encode());
                } else {
                    context.response().setStatusCode(404).end();
                }
            } else {
                context.fail(result.cause());
            }
        });
    }
}
