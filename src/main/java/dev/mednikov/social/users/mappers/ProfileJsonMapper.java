package dev.mednikov.social.users.mappers;

import dev.mednikov.social.users.models.User;
import io.vertx.core.json.JsonObject;

public class ProfileJsonMapper implements UserJsonMapper {

    private final boolean canSeeProfile;

    public ProfileJsonMapper(boolean canSeeProfile) {
        this.canSeeProfile = canSeeProfile;
    }

    @Override
    public JsonObject apply(User user) {
        JsonObject payload = new JsonObject();
        if (canSeeProfile) {
            payload.put("gender", user.getGender().toString());
            if (user.getDateOfBirth() != null){
                payload.put("dateOfBirth", user.getDateOfBirth().toString());
            }
            payload.put("summary", user.getSummary());
            payload.put("relationshipStatus", user.getRelationshipStatus().toString());
        }
        payload.put("id", user.getId().toString());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName", user.getLastName());
        payload.put("active", user.getActive());
        payload.put("avatarUrl", user.getAvatarUrl());
        payload.put("currentUser", false);
        payload.put("canSeeProfile", canSeeProfile);
        return payload;
    }

}
