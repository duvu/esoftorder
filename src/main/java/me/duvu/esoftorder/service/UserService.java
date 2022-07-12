package me.duvu.esoftorder.service;

import me.duvu.esoftorder.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link User}.
 */
public interface UserService {
    User save(User user);
    User update(User user);
    Optional<User> partialUpdate(User user);
    List<User> findAll();
    Optional<User> findOne(Long id);
    Optional<User> findByUsername(String username);
    void delete(Long id);
}
