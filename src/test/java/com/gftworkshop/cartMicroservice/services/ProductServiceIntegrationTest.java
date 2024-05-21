package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProductServiceIntegrationTest {

    ProductService productService;

    public ProductServiceIntegrationTest() {
        productService = new ProductService(RestClient.create());
    }

    @Test
    @DisplayName("when calling the Catalog Microservice, " +
            "then the service should return a Product")
    public void getProductById_RealService() {

        Product product = productService.getProductById(1L);

        assertEquals("Jacket", product.getName(), "The product name should match the expected value");
        assertEquals("Something indicate large central measure watch provide.", product.getDescription(), "The product description should match the expected value");
        assertEquals(26, product.getCurrent_stock(), "The product stock should match the expected value");
    }
}
