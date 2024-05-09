package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.CartProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartProductServiceImplTest {

    private CartProductRepository cartProductRepository;
    private CartProductService cartProductService;
    private CartProduct cartProduct;
    private Product product;

    @BeforeEach
    void setUp() {

        cartProductRepository = mock(CartProductRepository.class);

        cartProductService = new CartProductServiceImpl(cartProductRepository);

        cartProduct = mock(CartProduct.class);
        product = mock(Product.class);

    }

    @Test
    void productAddedIsSavedCorrectly() {

        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

        cartProductService.addProduct(product);

        verify(cartProductRepository).save(any(CartProduct.class));
    }

    @Test
    void productNameIsAssignedCorrectly() {

        Product product = new Product();
        product.setName("Airmax");
        cartProductService.addProduct(product);

        ArgumentCaptor<CartProduct> cartProductCaptor = ArgumentCaptor.forClass(CartProduct.class);
        verify(cartProductRepository).save(cartProductCaptor.capture());

        CartProduct savedCartProduct = cartProductCaptor.getValue();

        assertEquals("Airmax", savedCartProduct.getProductName());

    }
}
