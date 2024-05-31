package com.gftworkshop.cartMicroservice.cartmanagement;

import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.exceptions.CartProductInvalidQuantityException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CartValidator Unit Tests")
class CartValidatorTest {

    @Mock
    private ProductService productService;

    @Mock
    private CartProductRepository cartProductRepository;

    @InjectMocks
    private CartValidator cartValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test validateProductStock - enough stock")
    void testValidateProductStockEnoughStock() {
        // Given
        Cart cart = Cart.builder().id(1L).build();
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart); // Asigna un objeto Cart válido
        cartProduct.setQuantity(2);
        when(cartProductRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(new CartProduct()));
        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "Product", "Description", null, 5, null));

        // When / Then
        assertDoesNotThrow(() -> cartValidator.validateProductStock(cartProduct));
    }


    @Test
    @DisplayName("Test validateProductStock - not enough stock")
    void testValidateProductStockNotEnoughStock() {
        // Given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setQuantity(10);
        when(cartProductRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(new CartProduct()));
        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "Product", "Description", null, 5, null));

        // When / Then
        assertThrows(CartProductInvalidQuantityException.class, () -> cartValidator.validateProductStock(cartProduct));
    }

    @Test
    @DisplayName("Test calculateTotalDesiredQuantity")
    void testCalculateTotalDesiredQuantity() {
        // Given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setQuantity(2);
        when(cartProductRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(new CartProduct()));

        // When
        int totalDesiredQuantity = cartValidator.calculateTotalDesiredQuantity(cartProduct);

        // Then
        assertEquals(2, totalDesiredQuantity);
    }

    @Test
    @DisplayName("Test getCurrentQuantity - cart product found")
    void testGetCurrentQuantityCartProductFound() {
        // Given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(new Cart());
        cartProduct.setProductId(1L);
        cartProduct.setQuantity(2);
        when(cartProductRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(cartProduct));

        // When
        int currentQuantity = cartValidator.getCurrentQuantity(cartProduct);

        // Then
        assertEquals(2, currentQuantity);
    }

    @Test
    @DisplayName("Test getCurrentQuantity - cart product not found")
    void testGetCurrentQuantityCartProductNotFound() {
        // Given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(new Cart());
        cartProduct.setProductId(1L);
        when(cartProductRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // When
        int currentQuantity = cartValidator.getCurrentQuantity(cartProduct);

        // Then
        assertEquals(0, currentQuantity);
    }

    @Test
    @DisplayName("Test getAvailableStock")
    void testGetAvailableStock() {
        // Given
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductId(1L);
        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "Product", "Description", null, 5, null));

        // When
        int availableStock = cartValidator.getAvailableStock(cartProduct);

        // Then
        assertEquals(5, availableStock);
    }

    @Test
    @DisplayName("Test validateCartProductsStock - enough stock")
    void testValidateCartProductsStockEnoughStock() {
        // Given
        Cart cart = new Cart();
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductId(1L);
        cartProduct.setQuantity(2);
        cart.setCartProducts(Collections.singletonList(cartProduct));
        Product product = new Product(1L, "Product", "Description", null, 3, null);
        when(productService.findProductsByIds(anyList())).thenReturn(Collections.singletonList(product));

        // When / Then
        assertDoesNotThrow(() -> cartValidator.validateCartProductsStock(cart));
    }

    @Test
    @DisplayName("Test validateCartProductsStock - not enough stock")
    void testValidateCartProductsStockNotEnoughStock() {
        // Given
        Cart cart = new Cart();
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProductId(1L);
        cartProduct.setQuantity(5);
        cart.setCartProducts(Collections.singletonList(cartProduct));
        Product product = new Product(1L, "Product", "Description", null, 3, null);
        when(productService.findProductsByIds(anyList())).thenReturn(Collections.singletonList(product));

        // When / Then
        assertThrows(CartProductInvalidQuantityException.class, () -> cartValidator.validateCartProductsStock(cart));
    }

    @Test
    @DisplayName("Test getProductMap")
    void testGetProductMap() {
        // Given
        Cart cart = new Cart();
        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setProductId(1L);
        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setProductId(2L);
        cart.setCartProducts(Arrays.asList(cartProduct1, cartProduct2));
        Product product1 = new Product(1L, "Product 1", "Description 1", null, 5, null);
        Product product2 = new Product(2L, "Product 2", "Description 2", null, 10, null);
        when(productService.findProductsByIds(anyList())).thenReturn(Arrays.asList(product1, product2));

        // When
        Map<Long, Product> productMap = cartValidator.getProductMap(cart);

        // Then
        assertEquals(2, productMap.size());
        assertTrue(productMap.containsKey(1L));
        assertTrue(productMap.containsKey(2L));
        assertEquals(product1, productMap.get(1L));
        assertEquals(product2, productMap.get(2L));
    }
}
