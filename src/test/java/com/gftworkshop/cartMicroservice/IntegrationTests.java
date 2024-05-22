package com.gftworkshop.cartMicroservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class IntegrationTests {

    private static final Logger log = LoggerFactory.getLogger(IntegrationTests.class);
    @Autowired
    private WebTestClient client;

    @BeforeAll
    static void beforeAll() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductService productService = mock(ProductService.class);
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

    @Test
    void postCartProduct() {
        // Given
        Cart cart = Cart.builder().id(1L).userId(101L).build();

        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .id(5L)
                .productId(6L)
                .productName("Logitech Mouse")
                .productCategory("Electronics")
                .productDescription("Wireless Logitech Mouse M235")
                .quantity(2)
                .price(new BigDecimal("29.99"))
                .build();

        // When
        client.post().uri("/carts/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cartProduct)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.cart.id").isEqualTo(1L);
    }
}
