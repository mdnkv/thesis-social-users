package dev.mednikov.social.users.mappers;

import dev.mednikov.social.users.models.User;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

public interface UserJsonMapper extends Function<User, JsonObject> {}
