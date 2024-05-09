package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserService userService;
    private WebClient webClientMock;
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        webClientMock = Mockito.mock(WebClient.class);
        requestHeadersUriMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        when(webClientMock.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri(anyString(), Mockito.anyLong())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        userService = new UserService(webClientMock);
    }

    @Test
    void testGetUserById() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("john.doe@example.com");
        mockUser.setName("John");
        mockUser.setLastName("Doe");
        mockUser.setPassword("securepassword");
        mockUser.setFidelityPoints(500);
        mockUser.setBirthDate("1990-01-01");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setCountry(new Country(1L,"USA",21.0,"US","US"));

        when(responseSpecMock.bodyToMono(User.class)).thenReturn(Mono.just(mockUser));

        Mono<User> result = userService.getUserById(1L);

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getId().equals(1L) &&
                                user.getEmail().equals("john.doe@example.com") &&
                                user.getName().equals("John") &&
                                user.getLastName().equals("Doe") &&
                                user.getPassword().equals("securepassword") &&
                                user.getFidelityPoints().equals(500) &&
                                user.getBirthDate().equals("1990-01-01") &&
                                user.getPhoneNumber().equals("1234567890") &&
                                user.getCountry().getTax().equals(21.0)
                )
                .verifyComplete();
    }


    @Test
    void testGetUserByIdNotFound() {
        when(responseSpecMock.bodyToMono(User.class)).thenReturn(Mono.empty());

        Mono<User> result = userService.getUserById(999L);

        StepVerifier.create(result)
                .verifyComplete();
    }
}
