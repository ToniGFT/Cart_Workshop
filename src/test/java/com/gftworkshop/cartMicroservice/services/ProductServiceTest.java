package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.ExternalMicroserviceException;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
        productService.productUrl= "/catalog/products/{id}";
        productService.productsUrl= "/catalog/products/productsWithDiscount";
        productService.discountUrl = "/catalog/products/{product_id}/price-checkout?quantity={quantity}";
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
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Product not found")
                .addHeader("Content-Type", "text/plain"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductById(999L);
        });

    }

    @Test
    @DisplayName("When fetching a product by ID and an internal server error occurs, then a 500 error is returned")
    void testGetProductByIdServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductById(1L);
        });

    }

    @Test
    @DisplayName("When fetching discounted price, then the correct discounted price is returned")
    void testGetProductDiscountedPrice() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("75.0")
                .addHeader("Content-Type", "application/json"));

        float price = productService.getProductDiscountedPrice(1L, 2);
        assertEquals(75.0, price, 0.001);
    }

    @Test
    @DisplayName("When product ID does not exist for discount, then a 404 Not Found error is returned")
    void testGetProductDiscountedPriceNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Product discount not found")
                .addHeader("Content-Type", "text/plain"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductDiscountedPrice(999L, 1);
        });
    }

    @Test
    @DisplayName("When server error occurs during fetching discount, then a 500 error is returned")
    void testGetProductDiscountedPriceServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductDiscountedPrice(1L, 1);
        });
    }

    @Test
    @DisplayName("When the server returns malformed response, then handle the error gracefully")
    void testGetProductDiscountedPriceMalformedResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("not a float")
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .addHeader("Content-Type", "application/json"));

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductDiscountedPrice(1L, 3);
        });
    }

    @Test
    @DisplayName("When fetching products with discounted prices successfully, then return the correct products list")
    void testGetProductByIdWithDiscountedPriceSuccess() throws Exception {
        String productListJson = """
                [{
                    "id": 1,
                    "name": "Laptop",
                    "description": "High-end gaming laptop",
                    "price": 1080.00,
                    "stock": 10,
                    "category": "Electronics",
                    "discount": 0.10,
                    "weight": 2.5
                },
                {
                    "id": 2,
                    "name": "Laptop",
                    "description": "High-end gaming laptop",
                    "price": 1080.00,
                    "stock": 10,
                    "category": "Electronics",
                    "discount": 0.10,
                    "weight": 2.5
                }]
            """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(productListJson)
                .addHeader("Content-Type", "application/json"));

        Map<Long, Integer> productIdAmountMap = Map.of(1L, 2);
        List<Product> products = productService.getProductByIdWithDiscountedPrice(productIdAmountMap);

        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(2, products.size());
        Product product = products.get(0);
        assertEquals(1L, (long) product.getId());
        assertEquals(0, new BigDecimal("1080.00").compareTo(product.getPrice()));
    }

    @Test
    @DisplayName("When none of the product IDs exist, then a 404 Not Found error is returned")
    void testGetProductByIdWithDiscountedPriceNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("Products not found")
                .addHeader("Content-Type", "text/plain"));

        Map<Long, Integer> productIdAmountMap = Map.of(999L, 1);

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductByIdWithDiscountedPrice(productIdAmountMap);
        });
    }

    @Test
    @DisplayName("When server error occurs during fetching products with discounts, then a 500 error is returned")
    void testGetProductByIdWithDiscountedPriceServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "text/plain"));

        Map<Long, Integer> productIdAmountMap = Map.of(1L, 1);

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductByIdWithDiscountedPrice(productIdAmountMap);
        });
    }

    @Test
    @DisplayName("When the server returns a malformed response for products with discounts, then handle the error gracefully")
    void testGetProductByIdWithDiscountedPriceMalformedResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("not a valid list")
                .setResponseCode(HttpStatus.BAD_REQUEST.value())
                .addHeader("Content-Type", "application/json"));

        Map<Long, Integer> productIdAmountMap = Map.of(1L, 3);

        assertThrows(ExternalMicroserviceException.class, () -> {
            productService.getProductByIdWithDiscountedPrice(productIdAmountMap);
        });
    }





    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
}