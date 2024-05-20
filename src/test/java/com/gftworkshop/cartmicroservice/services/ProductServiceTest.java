package com.gftworkshop.cartmicroservice.services;

import com.gftworkshop.cartmicroservice.api.dto.Product;
import com.gftworkshop.cartmicroservice.exceptions.ExternalMicroserviceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ProductServiceTest {

    private MockWebServer mockWebServer;
    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();
        productService = new ProductService(restClient);
    }

    @Test
    @DisplayName("When fetching a product by ID, then the correct product details are returned")
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

        Product product = productService.getProductById(1L);

        assertNotNull(product);
        assertEquals(1L, (long) product.getId());
        assertEquals(0, new BigDecimal("1200.00").compareTo(product.getPrice()));
    }

    @Test
    @DisplayName("When fetching a product by ID and the product does not exist, then a 404 Not Found error is returned")
    void testGetProductByIdNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Product not found")
                .addHeader("Content-Type", "text/plain"));

        Exception exception = assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductById(999L);
        });

        assertEquals(HttpStatus.NOT_FOUND, ((ExternalMicroserviceException) exception).getStatusCode());
    }

    @Test
    @DisplayName("When fetching a product by ID and an internal server error occurs, then a 500 error is returned")
    void testGetProductByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        Exception exception = assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ((ExternalMicroserviceException) exception).getStatusCode());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}
