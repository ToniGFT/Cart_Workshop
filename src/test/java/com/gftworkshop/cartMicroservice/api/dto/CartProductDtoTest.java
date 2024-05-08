package com.gftworkshop.cartMicroservice.api.dto;


import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class CartProductDtoTest {

    @Test
    public void testGetAndSetId() {
        CartProductDto product = new CartProductDto();
        product.setId(1L);
        assertEquals(1L, product.getId());
    }

    @Test
    public void testGetAndSetProductName() {
        CartProductDto product = new CartProductDto();
        product.setProductName("Test Product");
        assertEquals("Test Product", product.getProductName());
    }

    @Test
    public void testGetAndSetProductCategory() {
        CartProductDto product = new CartProductDto();
        product.setProductCategory("Electronics");
        assertEquals("Electronics", product.getProductCategory());
    }

    @Test
    public void testGetAndSetProductDescription() {
        CartProductDto product = new CartProductDto();
        product.setProductDescription("Description here");
        assertEquals("Description here", product.getProductDescription());
    }

    @Test
    public void testGetAndSetQuantity() {
        CartProductDto product = new CartProductDto();
        product.setQuantity(5);
        assertEquals(5, product.getQuantity());
    }

    @Test
    public void testGetAndSetPrice() {
        CartProductDto product = new CartProductDto();
        product.setPrice(new BigDecimal("19.99"));
        assertEquals(new BigDecimal("19.99"), product.getPrice());
    }
}
