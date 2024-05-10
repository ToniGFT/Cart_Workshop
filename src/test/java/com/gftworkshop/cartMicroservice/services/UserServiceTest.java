package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.MockResponse;

import java.io.IOException;

public class UserServiceTest {

    private MockWebServer mockWebServer;
    private UserService userService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        userService = new UserService(webClient);
    }

    @Test
    @DisplayName("When fetching a user by ID, then the correct user details are returned")
    void testGetUserById() {
        String userJson = """
                {
                    "id": 100,
                    "email": "john.doe@example.com",
                    "name": "John",
                    "lastName": "Doe",
                    "password": "password123",
                    "fidelityPoints": 1000,
                    "birthDate": "1985-10-15",
                    "phoneNumber": "1234567890",
                    "country": {
                        "name": "USA",
                        "code": "US"
                    }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(userJson)
                .addHeader("Content-Type", "application/json"));

        Mono<User> userMono = userService.getUserById(100L);

        StepVerifier.create(userMono)
                .expectNextMatches(user ->
                        user.getId().equals(100L) &&
                                user.getEmail().equals("john.doe@example.com") &&
                                user.getName().equals("John"))
                .verifyComplete();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
