package com.gftworkshop.cartMicroservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartProductTest {

    private CartProduct cartProduct;
    private Cart cart;

    @BeforeEach
    public void setUp() {
        cart = mock(Cart.class);
        cartProduct = new CartProduct();
    }

    @Test
    @DisplayName("When getting cart, " +
            "then return associated cart")
    public void testCartProductAssociation() {

        cartProduct = new CartProduct(cart);

        assertEquals(cart, cartProduct.getCart());

    }

    @Test
    @DisplayName("When setting and getting product details, " +
            "then return expected values")
    public void testSettersAndGetters() {

        cartProduct.setProductName("Producto de prueba");
        cartProduct.setProductCategory("Categoría de prueba");
        cartProduct.setProductDescription("Descripción de prueba");
        cartProduct.setQuantity(2);
        cartProduct.setPrice(new BigDecimal("10.00"));

        assertEquals("Producto de prueba", cartProduct.getProductName());
        assertEquals("Categoría de prueba", cartProduct.getProductCategory());
        assertEquals("Descripción de prueba", cartProduct.getProductDescription());
        assertEquals(2, cartProduct.getQuantity());
        assertEquals(new BigDecimal("10.00"), cartProduct.getPrice());

    }
}
