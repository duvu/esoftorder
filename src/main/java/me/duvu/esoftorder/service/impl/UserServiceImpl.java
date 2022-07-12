package me.duvu.esoftorder.service.impl;

import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.repository.UserRepository;
import me.duvu.esoftorder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link User}.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        log.debug("Request to save User : {}", user);
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        log.debug("Request to save User : {}", user);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> partialUpdate(User user) {
        log.debug("Request to partially update User : {}", user);

        return userRepository
            .findById(user.getId())
            .map(existingUser -> {
                if (user.getUsername() != null) {
                    existingUser.setUsername(user.getUsername());
                }
                if (user.getPassword() != null) {
                    existingUser.setPassword(user.getPassword());
                }
                if (user.getFirstName() != null) {
                    existingUser.setFirstName(user.getFirstName());
                }
                if (user.getLastName() != null) {
                    existingUser.setLastName(user.getLastName());
                }
                if (user.getEmail() != null) {
                    existingUser.setEmail(user.getEmail());
                }
                if (user.getPhoneNumber() != null) {
                    existingUser.setPhoneNumber(user.getPhoneNumber());
                }
                if (user.getRole() != null) {
                    existingUser.setRole(user.getRole());
                }
                if (user.getLastLogin() != null) {
                    existingUser.setLastLogin(user.getLastLogin());
                }
                if (user.getCreatedAt() != null) {
                    existingUser.setCreatedAt(user.getCreatedAt());
                }
                if (user.getUpdatedAt() != null) {
                    existingUser.setUpdatedAt(user.getUpdatedAt());
                }

                return existingUser;
            })
            .map(userRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.debug("Request to get all UserES");
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findOne(Long id) {
        log.debug("Request to get User : {}", id);
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Request to get User : {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete User : {}", id);
        userRepository.deleteById(id);
    }
}
