package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql(scripts = {"/testdata.sql"})
class ControllerIntegrationTests {

    private ObjectMapper objectMapper;
    private CartDto expectedCart;
    @Autowired
    private WebTestClient client;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        expectedCart = CartDto.builder()
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

        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("GET - Tests for getting a cart by id")
    class GetCartByIdEndpoint {
        @Test
        void getCartByIdTest() {
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

        @Test
        void getCartById_BadRequestTest() {
            client.get().uri("/carts/{id}", "sads").exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody();
        }

        @Test
        void getCartById_CartNotFoundTest() {
            //When
            client.get().uri("/carts/{id}", -3).exchange()
                    .expectStatus().isNotFound()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody();
        }
    }

    @Nested
    @DisplayName("POST - Tests for adding a cart by user id")
    class addCartByUserIdEndpoint {

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
    @DisplayName("DELETE - Test for removing a cart by id")
    class removeCartByIdEndpoint {

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
            String cartId = "prueba";

            client.delete().uri("/carts/{id}", cartId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }

        @Test
        void removeCartById_BadRequest_DoubleTest() {
            Double cartId = 1.1;

            client.delete().uri("/carts/{id}", cartId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }
    }

    @Nested
    @DisplayName("PATCH - Updating quantity of a product")
    class UpdateProductQuantityEndpoint {
        @Test
        void removeCartProductByIdTest() {
            Long cartProductId = 1L;

            client.delete().uri("/carts/products/{id}", cartProductId).exchange()
                    .expectStatus()
                    .isOk();
        }

        @Test
        void removeCartProductById_NotFoundTest() {
            Long cartProductId = 9999L;

            client.delete().uri("/carts/{id}", cartProductId).exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void removeCartProductById_BadRequest_StringTest() {
            String cartProductId = "prueba";

            client.delete().uri("/carts/{id}", cartProductId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }

        @Test
        void removeCartProductById_BadRequest_DoubleTest() {
            Double cartProductId = 1.1;

            client.delete().uri("/carts/{id}", cartProductId).exchange()
                    .expectStatus()
                    .isBadRequest();
        }
    }

}