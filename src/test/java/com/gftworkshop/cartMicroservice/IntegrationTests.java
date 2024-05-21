package com.gftworkshop.cartMicroservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IntegrationTests {

    @Autowired
    private WebTestClient client;

    private static ObjectMapper objectMapper;
    private static MockWebServer mockWebServer;
    private static ProductService productService;

    @BeforeAll
    static void beforeAll() throws IOException {
        objectMapper = new ObjectMapper();
        mockWebServer = new MockWebServer();
        mockWebServer.start(8081);
        productService = mock(ProductService.class);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.close();
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
    void postCartProduct() throws JsonProcessingException {

        CartProduct cartProduct = CartProduct.builder()
                .id(2L)
                .productId(2L)
                .productName("Logitech Mouse")
                .productCategory("Electronics")
                .productDescription("Wireless Logitech Mouse M235")
                .quantity(2)
                .price(new BigDecimal("29.99"))
                .build();

        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "prodName", "description", new BigDecimal("100"), 100, "category", 100.0));

        client.post().uri("/carts/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cartProduct)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CartProduct.class)
                .value(cartProductResponse -> {
                    assertThat(cartProductResponse).isEqualTo(cartProduct);
                });


    }

}