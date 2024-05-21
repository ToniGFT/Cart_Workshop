package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ControllerIntegrationTests {

    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient client;


    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getCartByIdTest() {
        //Given
        CartDto expectedCart = CartDto.builder()
                .id(1L)
                .userId(101L)
                .cartProducts(Arrays.asList(
                        CartProduct.builder()
                                .id(1L)
                                .productId(1L)
                                .productName("Apple MacBook Pro")
                                .productCategory("Electronics")
                                .productDescription("Latest model of Apple MacBook Pro 16 inch.")
                                .quantity(1)
                                .price(new BigDecimal("2399.99"))
                                .build(),
                        CartProduct.builder()
                                .id(2L)
                                .productId(2L)
                                .productName("Logitech Mouse")
                                .productCategory("Electronics")
                                .productDescription("Wireless Logitech Mouse M235")
                                .quantity(2)
                                .price(new BigDecimal("29.99"))
                                .build()
                ))
                .build();

        //When
        client.get().uri("/carts/{id}", 1L).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CartDto.class)
                .value(cartDto -> {
                    //Then
                    assertThat(cartDto).isEqualTo(expectedCart);
                });

    }

    @Nested
    @DisplayName("Tests for adding a cart by user id")
    class addCartByUserIdEndpoint{

        @Test
        void addCartByUserIdTest() {
            Long userId = 104L;
            CartDto expectedCart = CartDto.builder()
                    .userId(userId)
                    .cartProducts(null)
                    .build();

            client.post().uri("/carts/{id}", userId).exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(CartDto.class)
                    .value(cartDto -> {
                        assertThat(cartDto.getUserId()).isEqualTo(expectedCart.getUserId());
                        assertThat(cartDto.getCartProducts()).isEqualTo(expectedCart.getCartProducts());
                    });
        }

        @Test
        void addCartByUserId_NotFoundTest() {
            Long userId = 101L;

            client.post().uri("/carts/{id}", userId).exchange()
                    .expectStatus()
                    .is5xxServerError();
        }

        @Test
        void addCartByUserId_BadRequest_StringTest() {
            String userId = "prueba";

            client.post().uri("/carts/{id}", userId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }

        @Test
        void addCartByUserId_BadRequest_DoubleTest() {
            Double userId = 1.1;

            client.post().uri("/carts/{id}", userId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }

    }

    @Nested
    @DisplayName("Test for removing a cart by id")
    class removeCartByIdEndpoint{

        @Test
        void removeCartByIdTest() {
            Long cartId = 1L;

            client.delete().uri("/carts/{id}", cartId).exchange()
                    .expectStatus().isOk();
        }

        @Test
        void removeCartById_NotFoundTest() {
            Long cartId = 9999L;

            client.delete().uri("/carts/{id}", cartId).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void removeCartById_BadRequest_StringTest() {
            String userId = "prueba";

            client.delete().uri("/carts/{id}", userId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }

        @Test
        void removeCartById_BadRequest_DoubleTest() {
            Double userId = 1.1;

            client.delete().uri("/carts/{id}", userId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }
    }


}