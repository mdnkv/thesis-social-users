package dev.mednikov.social.users.data;

import dev.mednikov.social.users.models.User;
import io.vertx.core.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class UserRepositoryImpl implements UserRepository {

    private final List<User> users;

    public UserRepositoryImpl() {
        this.users = new ArrayList<>();
    }

    @Override
    public Future<Optional<User>> findByAuthId(UUID authId) {
        Optional<User> result = this.users.stream().filter(u -> u.getAuthId().equals(authId)).findFirst();
        return Future.succeededFuture(result);
    }

    @Override
    public Future<Optional<User>> findById(Long id) {
        Optional<User> result = this.users.stream().filter(u -> u.getId().equals(id)).findFirst();
        return Future.succeededFuture(result);
    }

    @Override
    public Future<User> createUser(User user) {
        Long id = users.size() + 1L;
        user.setId(id);
        users.add(user);
        return Future.succeededFuture(user);
    }

    @Override
    public Future<User> updateUser(User user) {
        return null;
    }

}
