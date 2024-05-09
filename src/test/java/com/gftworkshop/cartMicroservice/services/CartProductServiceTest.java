package com.gftworkshop.cartMicroservice.services;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.CartService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CartProductServiceTest {

    @Test
    void testAddProductToCart() {

        CartRepository cartRepository = mock(CartRepository.class);
        CartProductRepository cartProductRepository = mock(CartProductRepository.class);

        Cart cart = mock(Cart.class);

        CartService cartService = mock(CartService.class);

        CartProduct cartProduct = new CartProduct(cart);
        cartProduct.setProductName("Product 1");
        cartService.addProductToCart(cart, cartProduct);

        ArgumentCaptor<CartProduct> cartProductCaptor = ArgumentCaptor.forClass(CartProduct.class);
        verify(cartProductRepository).save(cartProductCaptor.capture());
        assertEquals(cart, cartProductCaptor.getValue().getCart());
        assertEquals("Product 1", cartProductCaptor.getValue().getProductName());
    }
}
