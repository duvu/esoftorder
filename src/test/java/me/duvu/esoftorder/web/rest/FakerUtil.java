package me.duvu.esoftorder.web.rest;

import com.github.javafaker.Faker;
import me.duvu.esoftorder.domain.Order;
import me.duvu.esoftorder.domain.User;
import me.duvu.esoftorder.domain.enumeration.OrderCategory;
import me.duvu.esoftorder.domain.enumeration.Role;
import me.duvu.esoftorder.domain.enumeration.Service;

import java.time.Instant;
import java.util.Random;

public class FakerUtil {
    private static Faker faker = new Faker();
    private static final Random RANDOM = new Random();

    public static User createUser() {
        return new User()
                .username(faker.name().username())
                .password(faker.hacker().verb())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .role(randRol())
                .lastLogin(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now());
    }

    public static Order createAnOrder() {
        return new Order()
                .reference(faker.lorem().characters(32))
                .category(randCat())
                .quantity(faker.number().randomDigitNotZero())
                .price(faker.number().randomDouble(2, 1, 1000))
                .serviceName(randSer())
                .description(faker.lorem().characters(64))
                .notes(faker.lorem().characters(64))
                .createdAt(Instant.now())
                .updatedAt(Instant.now());
    }

    private static OrderCategory randCat() {
        int idx = RANDOM.nextInt(3);
        return idx == 0 ? OrderCategory.LUXURY : idx == 1 ? OrderCategory.SUPER_LUXURY : OrderCategory.SUPREME_LUXURY;
    }

    private static Service randSer() {
        int idx = RANDOM.nextInt(2);
        return idx == 0 ? Service.PHOTO_EDITING : Service.VIDEO_EDITING;
    }

    private static Role randRol() {
        int idx = RANDOM.nextInt(2);
        return idx == 0 ? Role.ADMIN : Role.CUSTOMER;
    }
}
