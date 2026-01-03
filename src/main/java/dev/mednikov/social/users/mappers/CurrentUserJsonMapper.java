package dev.mednikov.social.users.mappers;

import dev.mednikov.social.users.models.User;
import io.vertx.core.json.JsonObject;

public final class CurrentUserJsonMapper implements UserJsonMapper {

    @Override
    public JsonObject apply(User user) {
        JsonObject payload = new JsonObject();
        payload.put("id", user.getId().toString());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("active", user.getActive());
        payload.put("avatarUrl", user.getAvatarUrl());
        payload.put("currentUser", true);
        payload.put("canSeeProfile", true);
        payload.put("gender", user.getGender().toString());
        payload.put("summary", user.getSummary());
        payload.put("relationshipStatus", user.getRelationshipStatus().toString());
        if (user.getDateOfBirth() != null){
            payload.put("dateOfBirth", user.getDateOfBirth().toString());
        }
        return payload;
    }
}
