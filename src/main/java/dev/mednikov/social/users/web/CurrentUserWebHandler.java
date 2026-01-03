package dev.mednikov.social.users.web;

import dev.mednikov.social.users.data.UserRepository;
import dev.mednikov.social.users.mappers.CurrentUserJsonMapper;
import dev.mednikov.social.users.mappers.UserJsonMapper;
import dev.mednikov.social.users.models.User;
import dev.mednikov.social.users.models.UserGender;
import dev.mednikov.social.users.models.UserRelationshipStatus;
import dev.mednikov.social.users.utils.UserUtils;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

class CurrentUserWebHandler implements Handler<RoutingContext> {

    private final static Logger logger = LoggerFactory.getLogger(CurrentUserWebHandler.class);

    private final UserRepository userRepository;

    public CurrentUserWebHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(RoutingContext context) {
        JsonObject principal = context.user().principal();
        UUID authId = UUID.fromString(principal.getString("sub"));
        this.userRepository.findByAuthId(authId).compose(result -> {
            if (result.isPresent()){
                User originalUser = result.get();
                if (checkForUpdate(principal, originalUser)){
                    // update existing user
                    originalUser.setEmail(principal.getString("email"));
                    originalUser.setAvatarUrl(UserUtils.getGravatarUrl(originalUser.getEmail()));
                    originalUser.setFirstName(principal.getString("given_name"));
                    originalUser.setLastName(principal.getString("family_name"));
                    originalUser.setEmailVerified(principal.getBoolean("email_verified", false));
                    return this.userRepository.updateUser(originalUser);
                } else {
                    return Future.succeededFuture(originalUser);
                }
            } else {
                // Create user
                User user = new User();
                user.setAuthId(authId);
                user.setEmail(principal.getString("email"));
                user.setFirstName(principal.getString("given_name"));
                user.setLastName(principal.getString("family_name"));
                user.setAvatarUrl(UserUtils.getGravatarUrl(principal.getString("email")));
                user.setEmailVerified(principal.getBoolean("email_verified", false));
                user.setActive(true);
                user.setPrivateProfile(false);
                user.setRelationshipStatus(UserRelationshipStatus.UNKNOWN);
                user.setGender(UserGender.UNKNOWN);
                return this.userRepository.createUser(user);
            }
        }).onComplete(result -> {
            if (result.succeeded()){
                User user = result.result();
                UserJsonMapper mapper = new CurrentUserJsonMapper();
                JsonObject payload = mapper.apply(user);
                context.response().setStatusCode(200).end(payload.encode());
            } else {
                context.fail(result.cause());
            }
        });
    }

    boolean checkForUpdate (JsonObject principal, User originalUser){
        boolean result = !originalUser.getEmail().equals(principal.getString("email"));
        if (!originalUser.getFirstName().equals(principal.getString("given_name"))){
            result = true;
        }
        if (!originalUser.getLastName().equals(principal.getString("family_name"))){
            result = true;
        }
        if (!originalUser.getEmailVerified().equals(principal.getBoolean("email_verified", false))){
            result = true;
        }
        return result;
    }
}
