package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private RestClient restClient;
    private UserService userService;

    @BeforeEach
    void setUp() {
        restClient = Mockito.mock(RestClient.class);
        userService = new UserService(restClient);
    }

    @Nested
    @DisplayName("When getUserById")
    class WhenGetUserById {

        @Test
        @DisplayName("Given a valid ID then return User")
        void getUserByIdWithValidId() {
            Country country = Country.builder().tax(11.0).build();
            User expectedUser = User.builder().id(1L).country(country).build();
            RestClient.RequestHeadersUriSpec request = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec response = Mockito.mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(request);
            when(request.uri(any(String.class), anyLong())).thenReturn(request);
            when(request.retrieve()).thenReturn(response);

            when(response.onStatus(any(), any())).thenReturn(response);

            when(response.body(User.class)).thenReturn(expectedUser);

            User actualUser = userService.getUserById(1L);

            assertThat(actualUser).isNotNull();
            assertThat(actualUser).isEqualTo(expectedUser);
        }


        @Test
        @DisplayName("Given an invalid ID then throw ExternalMicroserviceException")
        void getUserByIdWithInvalidId() {
            RestClient.RequestHeadersUriSpec request = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
            RestClient.ResponseSpec response = Mockito.mock(RestClient.ResponseSpec.class);

            when(restClient.get()).thenReturn(request);

            when(request.uri(any(String.class), anyLong())).thenReturn(request);
            when(request.retrieve()).thenReturn(response);
            when(response.onStatus(any(), any())).thenThrow(new ExternalMicroserviceException("USER MICROSERVICE EXCEPTION: Not Found"));

            assertThatThrownBy(() -> userService.getUserById(-1L))
                    .isInstanceOf(ExternalMicroserviceException.class)
                    .hasMessageContaining("USER MICROSERVICE EXCEPTION: Not Found");
        }
    }
}
