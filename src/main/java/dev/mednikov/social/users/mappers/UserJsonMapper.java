package dev.mednikov.social.users.mappers;

import dev.mednikov.social.users.models.User;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public final class UserJsonMapper implements Function<User, JsonObject> {

    @Override
    public JsonObject apply(User user) {
        JsonObject payload = new JsonObject();
        payload.put("id", user.getId().toString());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("active", user.getActive());
        payload.put("avatarUrl", user.getAvatarUrl());
        return payload;
    }

}
