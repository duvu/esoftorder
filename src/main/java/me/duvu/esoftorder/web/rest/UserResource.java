package me.duvu.esoftorder.web.rest;

import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.repository.UserRepository;
import me.duvu.esoftorder.security.AuthoritiesConstants;
import me.duvu.esoftorder.security.jwt.TokenProvider;
import me.duvu.esoftorder.service.UserService;
import me.duvu.esoftorder.util.HeaderUtil;
import me.duvu.esoftorder.util.ResponseUtil;
import me.duvu.esoftorder.web.rest.errors.BadRequestAlertException;
import me.duvu.esoftorder.web.rest.vm.AuthVM;
import me.duvu.esoftorder.web.rest.vm.TokenVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link User}.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private static final String ENTITY_NAME = "TblUser";

    @Value("${app.clientApp.name}")
    private String applicationName;

    private final TokenProvider tokenProvider;

    private final UserService userService;

    private final UserRepository userRepository;

    public UserResource(TokenProvider tokenProvider, UserService userService, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<TokenVM> authenticate(@RequestBody AuthVM auth) throws URISyntaxException {
        User user = this.userService.findByUsername(auth.getUsername()).orElse(null);
        if (user != null && user.getPassword().equals(auth.getPassword())) {
            // Return Token
            TokenVM token = new TokenVM();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    auth.getUsername(),
                    auth.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(AuthoritiesConstants.USER))
            );
            String accessToken = this.tokenProvider.createToken(authentication, user.getId(), auth.getRememberMe());
            token.setAccess_token(accessToken);
            return ResponseEntity.ok(token);
        } else {
            throw new BadRequestAlertException("User not found", ENTITY_NAME, "");
        }
    }

    /**
     * {@code POST  /users} : Create a new user.
     */
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save User : {}", user);
        if (user.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", ENTITY_NAME, "idexists");
        }
        User result = userService.save(user);
        return ResponseEntity
            .created(new URI("/api/users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id", required = false) final Long id, @RequestBody User user)
        throws URISyntaxException {
        log.debug("REST request to update User : {}, {}", id, user);
        if (user.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        User result = userService.update(user);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, user.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<User> partialUpdateUser(@PathVariable(value = "id", required = false) final Long id, @RequestBody User user)
        throws URISyntaxException {
        log.debug("REST request to partial update User partially : {}, {}", id, user);
        if (user.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, user.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<User> result = userService.partialUpdate(user);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, user.getId().toString())
        );
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        log.debug("REST request to get all UserES");
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.debug("REST request to get User : {}", id);
        Optional<User> user = userService.findOne(id);
        return ResponseUtil.wrapOrNotFound(user);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("REST request to delete User : {}", id);
        userService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
