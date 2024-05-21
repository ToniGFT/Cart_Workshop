package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceIntegrationTest {

    UserService userService;

    public UserServiceIntegrationTest() {
        userService = new UserService(RestClient.create());
    }

    @Test
    @DisplayName("when calling the User Microservice, " +
            "then the service should return a User")
    public void getUserById_RealService() {

        User user = userService.getUserById(1L);

        assertEquals(21.0, user.getCountry().getTax(), "The user's tax should match the expected value");
    }
}
