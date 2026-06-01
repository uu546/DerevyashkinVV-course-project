package project.warehouse.foundation.interfaces;

import project.warehouse.entity.User;

import java.util.Optional;

public interface IUserRepository {

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}