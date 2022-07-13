package me.duvu.esoftorder.web.rest;

import me.duvu.esoftorder.domain.Order;
import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.domain.enumeration.Role;
import me.duvu.esoftorder.repository.OrderRepository;
import me.duvu.esoftorder.repository.UserRepository;
import me.duvu.esoftorder.web.rest.vm.AuthVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class ReportResourceTests {
    private static final String API_AUTH_URL = "/api/authenticate";
    private static final String API_REPORT_URL = "/api/report";
    private static final String API_REPORT_SUMMARY_BY_USER_URL = "/api/report/summaryByUser/{userId}";
    private static final String API_REPORT_SUMMARY_URL = "/api/report/summary";

    private static final String API_ORDER_URL = "/api/orders";

    //--User--
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMockMvc;
    private User user;

    private String getAccessToken(String userName, String password) throws Exception {
        AuthVM vm = new AuthVM();
        vm.setUsername(userName);
        vm.setPassword(password);
        vm.setRememberMe(false);

        ResultActions result = restMockMvc.
                perform(post(API_AUTH_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(vm)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.access_token").isString());

        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }


    public static User createUser(EntityManager em) {
        return new User()
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
    }

    @BeforeEach
    public void initTest() {
        user = createUser(em);
        user = userRepository.save(user);

        // clean order table
        orderRepository.deleteAll();
    }

    @Test
    @Transactional
    void summaryByUser() throws Exception {
        String accessToken = getAccessToken(user.getUsername(), user.getPassword());
        // 1. create user
        // 2. create several order
        Order order1 = FakerUtil.createAnOrder();
        order1.setUser(user);
        orderRepository.saveAndFlush(order1);

        // 3. query report and check
        Double revenue = order1.getQuantity() * order1.getPrice();
        restMockMvc.perform(get(API_REPORT_SUMMARY_BY_USER_URL, user.getId())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfOrder").value(1))
                .andExpect(jsonPath("$.revenue").value(revenue));
    }

    @Test
    @Transactional
    void summaryByNonExistingUser() throws Exception {
        String accessToken = getAccessToken(user.getUsername(), user.getPassword());

        restMockMvc.perform(get(API_REPORT_SUMMARY_BY_USER_URL, Long.MAX_VALUE)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void summary() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        String accessToken = getAccessToken(user.getUsername(), user.getPassword());

        Order order1 = FakerUtil.createAnOrder();
        order1.setUser(user);
        orderRepository.saveAndFlush(order1);

        Order order2 = FakerUtil.createAnOrder();
        order2.setUser(user);
        orderRepository.saveAndFlush(order2);

        Order order3 = FakerUtil.createAnOrder();
        order3.setUser(user);
        orderRepository.saveAndFlush(order3);

        // ---
        Double revenue = order1.getQuantity() * order1.getPrice() +
                order2.getQuantity() * order2.getPrice() +
                order3.getQuantity() * order3.getPrice() ;
        restMockMvc.perform(get(API_REPORT_SUMMARY_URL)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfOrder").value(databaseSizeBeforeCreate + 3))
                .andExpect(jsonPath("$.revenue").value(revenue));
    }


    @Test
    @Transactional
    void summarySpecificTime() throws Exception {
        String accessToken = getAccessToken(user.getUsername(), user.getPassword());

        Instant fromDT = Instant.parse("2018-11-30T18:35:24.00Z");
        Instant toDT = Instant.parse("2018-12-30T18:35:24.00Z");

        Instant createdDT1 = Instant.parse("2018-12-01T18:35:24.00Z");
        Instant createdDT2 = Instant.parse("2018-12-02T18:35:24.00Z");
        Instant createdDT3 = Instant.parse("2018-10-03T18:35:24.00Z");

        Order order1 = FakerUtil.createAnOrder();
        order1.setUser(user);
        order1.setCreatedAt(createdDT1);
        orderRepository.saveAndFlush(order1);

        Order order2 = FakerUtil.createAnOrder();
        order2.setUser(user);
        order2.setCreatedAt(createdDT2);
        orderRepository.saveAndFlush(order2);

        Order order3 = FakerUtil.createAnOrder();
        order3.setUser(user);
        order3.setCreatedAt(createdDT3);
        orderRepository.saveAndFlush(order3);

        // ---
        Double revenue = order1.getQuantity() * order1.getPrice() +
                order2.getQuantity() * order2.getPrice();
        restMockMvc.perform(get(API_REPORT_SUMMARY_URL + "?from=" + fromDT.getEpochSecond()*1000 + "&to=" + toDT.getEpochSecond()*1000)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfOrder").value(2))
                .andExpect(jsonPath("$.revenue").value(revenue));
    }
}
