package me.duvu.esoftorder.web.rest;

import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.domain.enumeration.Role;
import me.duvu.esoftorder.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class UserResourceTests {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final Role DEFAULT_ROLE = Role.ADMIN;
    private static final Role UPDATED_ROLE = Role.CUSTOMER;

    private static final Instant DEFAULT_LAST_LOGIN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_LOGIN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserMockMvc;

    private User user;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createEntity(EntityManager em) {
        User user = new User()
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .role(DEFAULT_ROLE)
            .lastLogin(DEFAULT_LAST_LOGIN)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return user;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static User createUpdatedEntity(EntityManager em) {
        User user = new User()
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .role(UPDATED_ROLE)
            .lastLogin(UPDATED_LAST_LOGIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return user;
    }

    @BeforeEach
    public void initTest() {
        user = createEntity(em);
    }

    @Test
    @Transactional
    void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();
        // Create the User
        restUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(user)))
            .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testUser.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testUser.getLastLogin()).isEqualTo(DEFAULT_LAST_LOGIN);
        assertThat(testUser.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testUser.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createUserWithExistingId() throws Exception {
        // Create the User with an existing ID
        user.setId(1L);

        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(user)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserES() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the userList
        restUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(user.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
            .andExpect(jsonPath("$.[*].lastLogin").value(hasItem(DEFAULT_LAST_LOGIN.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc
            .perform(get(ENTITY_API_URL_ID, user.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(user.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.lastLogin").value(DEFAULT_LAST_LOGIN.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingUser() throws Exception {
        // Get the user
        restUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).get();
        // Disconnect from session so that the updates on updatedUser are not directly saved in db
        em.detach(updatedUser);
        updatedUser
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .role(UPDATED_ROLE)
            .lastLogin(UPDATED_LAST_LOGIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUser))
            )
            .andExpect(status().isOk());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testUser.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testUser.getLastLogin()).isEqualTo(UPDATED_LAST_LOGIN);
        assertThat(testUser.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUser.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void putNonExistingUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        user.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(user))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        user.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(user))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        user.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(user)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserWithPatch() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user using partial update
        User partialUpdatedUser = new User();
        partialUpdatedUser.setId(user.getId());

        partialUpdatedUser
            .username(UPDATED_USERNAME)
            .firstName(UPDATED_FIRST_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUser))
            )
            .andExpect(status().isOk());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testUser.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testUser.getLastLogin()).isEqualTo(DEFAULT_LAST_LOGIN);
        assertThat(testUser.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUser.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateUserWithPatch() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user using partial update
        User partialUpdatedUser = new User();
        partialUpdatedUser.setId(user.getId());

        partialUpdatedUser
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .role(UPDATED_ROLE)
            .lastLogin(UPDATED_LAST_LOGIN)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUser))
            )
            .andExpect(status().isOk());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testUser.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testUser.getLastLogin()).isEqualTo(UPDATED_LAST_LOGIN);
        assertThat(testUser.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testUser.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        user.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, user.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(user))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        user.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(user))
            )
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();
        user.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(user)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Delete the user
        restUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, user.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
