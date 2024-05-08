package com.gftworkshop.cartMicroservice.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;

public class CartDtoTest {

    @Test
    public void testGetAndSetId() {
        CartDto cart = new CartDto();
        cart.setId(1L);
        assertEquals(1L, cart.getId());
    }

    @Test
    public void testGetAndSetCartProducts() {
        CartDto cart = new CartDto();
        List<CartProductDto> cartProducts = new ArrayList<>();
        cartProducts.add(new CartProductDto());
        cart.setCartProducts(cartProducts);

        assertEquals(1, cart.getCartProducts().size());
    }
}

