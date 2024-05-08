package com.gftworkshop.cartMicroservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    public void testGetAndSetProductId() {
        Product product = new Product();
        product.setProductId(1L);
        assertEquals(1L, product.getProductId(), "Failed to get/set the product ID.");
    }

    @Test
    public void testGetAndSetName() {
        Product product = new Product();
        product.setName("Gaming Laptop");
        assertEquals("Gaming Laptop", product.getName(), "Failed to get/set the product name.");
    }

    @Test
    public void testGetAndSetDescription() {
        Product product = new Product();
        product.setDescription("High performance for gaming.");
        assertEquals("High performance for gaming.", product.getDescription(), "Failed to get/set the description.");
    }

    @Test
    public void testGetAndSetPrice() {
        Product product = new Product();
        product.setPrice(new BigDecimal("1200.00"));
        assertEquals(new BigDecimal("1200.00"), product.getPrice(), "Failed to get/set the price.");
    }

    @Test
    public void testGetAndSetStock() {
        Product product = new Product();
        product.setStock(100L);
        assertEquals(100L, product.getStock(), "Failed to get/set the stock quantity.");
    }

    @Test
    public void testGetAndSetDiscount() {
        Product product = new Product();
        product.setDiscount(new BigDecimal("0.15"));
        assertEquals(new BigDecimal("0.15"), product.getDiscount(), "Failed to get/set the discount.");
    }

    @Test
    public void testGetAndSetCategory() {
        Product product = new Product();
        product.setCategory("Electronics");
        assertEquals("Electronics", product.getCategory(), "Failed to get/set the category.");
    }
}
