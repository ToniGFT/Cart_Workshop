package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.math.BigDecimal;

public class ProductServiceTest {

    private MockWebServer mockWebServer;
    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        productService = new ProductService(webClient);
    }

    @Test
    @DisplayName("When fetching a product by ID, " +
            "then the correct product details are returned")
    void testGetProductById() {
        String productJson = """
                {
                    "id": 1,
                    "name": "Laptop",
                    "description": "High-end gaming laptop",
                    "price": 1200.00,
                    "stock": 10,
                    "category": "Electronics",
                    "discount": 0.10,
                    "weight": 2.5
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(productJson)
                .addHeader("Content-Type", "application/json"));

        Mono<Product> productMono = productService.getProductById(1L);

        StepVerifier.create(productMono)
                .expectNextMatches(product ->
                        product.getId().equals(1L) &&
                                product.getName().equals("Laptop") &&
                                product.getPrice().compareTo(new BigDecimal("1200.00")) == 0)
                .verifyComplete();
    }

    @Test
<<<<<<< HEAD
    @DisplayName("When fetching a product by ID and the product does not exist, then a 404 Not Found error is returned")
=======
    @DisplayName("When fetching a product by ID and the product does not exist," +
            " then a 404 Not Found error is returned")
>>>>>>> main
    void testGetProductByIdNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Product not found")
                .addHeader("Content-Type", "text/plain"));

        Mono<Product> productMono = productService.getProductById(999L);

        StepVerifier.create(productMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    @Test
<<<<<<< HEAD
    @DisplayName("When fetching a product by ID and an internal server error occurs, then a 500 error is returned")
=======
    @DisplayName("When fetching a product by ID and an internal server error occurs," +
            " then a 500 error is returned")
>>>>>>> main
    void testGetProductByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        Mono<Product> productMono = productService.getProductById(1L);

        StepVerifier.create(productMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof WebClientResponseException &&
                                ((WebClientResponseException) throwable).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verify();
    }



    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
