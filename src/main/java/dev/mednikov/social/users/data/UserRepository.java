package dev.mednikov.social.users.data;

import dev.mednikov.social.users.models.User;
import io.vertx.core.Future;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Future<Optional<User>> findByAuthId (UUID authId);

    Future<User> createUser (User user);

    Future<User> updateUser (User user);

}
