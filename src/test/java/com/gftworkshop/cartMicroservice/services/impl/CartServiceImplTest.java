package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.exceptions.*;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;

    @Test
    @DisplayName("Given a cart and a product, when adding the product to the cart, if there isn't enough stock, then a CartProductInvalidQuantityException should be thrown")
    void addProductToCart_NotEnoughStockExceptionTest() {

        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();

        CartProduct cartProduct = CartProduct.builder()
                .id(1L)
                .productId(1L)
                .quantity(1000)
                .cart(cart)
                .build();

        Product product = Product.builder()
                .id(1L)
                .currentStock(100)
                .build();

        lenient().when(productService.getProductById(1L)).thenReturn(product);
        lenient().when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        CartProductInvalidQuantityException exception = assertThrows(CartProductInvalidQuantityException.class, () -> cartServiceImpl.addProductToCart(cartProduct));

        assertEquals("Not enough stock to add product to cart. Desired amount: 1000. Actual stock: 100", exception.getMessage());
    }

    @Test
    @DisplayName("Given a cart and a product, when adding the product to the cart, then the product should be added successfully")
    void addProductToCartTest() {
        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();
        CartProduct cartProduct = CartProduct.builder().id(1L).productId(1L).quantity(10).cart(cart).build();

        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "prodName", "description", new BigDecimal("100"), 100, 100.0));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartServiceImpl.addProductToCart(cartProduct);

        verify(cartProductRepository, times(1)).save(any());
    }


    @Test
    @DisplayName("Given a cart and a product, when adding the product to the cart, if there isn't enough stock, an exception should be thrown")
    void addProductToCartTestNotEnoughStock() {
        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();

        CartProduct cartProduct = CartProduct.builder()
                .id(1L)
                .productId(1L)
                .quantity(1000)
                .cart(cart)
                .build();

        Product product = Product.builder()
                .id(1L)
                .currentStock(100)
                .build();

        when(productService.getProductById(1L)).thenReturn(product);

        assertThrows(
                CartProductInvalidQuantityException.class,
                () -> cartServiceImpl.addProductToCart(cartProduct)
        );
    }


    @Test
    @DisplayName("Given a cart with multiple products and a user, when calculating the cart total including tax and shipping, then the total should be calculated correctly")
    void getCartTotalTest() {
        Long userId = 1L;
        Long cartId = 1L;
        User user = User.builder().country(new Country(1L, 10.0)).id(userId).build();
        List<CartProduct> cartProducts = List.of(
                CartProduct.builder().productId(1L).quantity(1).price(new BigDecimal("10")).build(),
                CartProduct.builder().productId(2L).quantity(1).price(new BigDecimal("20")).build()
        );
        Cart cart = Cart.builder().id(cartId).userId(userId).cartProducts(cartProducts).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(user);

        Product product1 = Product.builder().id(1L).price(new BigDecimal("10")).weight(1.0).build();
        Product product2 = Product.builder().id(2L).price(new BigDecimal("20")).weight(1.0).build();
        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        when(productService.getProductByIdWithDiscountedPrice(anyList())).thenReturn(products);


        BigDecimal expectedTotal = new BigDecimal("38.0"); // Total products = 10+20, Tax = 10%, Shipping = 5
        BigDecimal cartTotal = cartServiceImpl.calculateCartTotal(cartId, userId);

        assertEquals(expectedTotal, cartTotal);
    }


    @Test
    @DisplayName("Given a total weight, when calculating the weight cost, then the cost should be calculated correctly according to weight ranges")
    void calculateWeightCostTest() {
        assertEquals(new BigDecimal("5"), cartServiceImpl.computeShippingCost(3));
        assertEquals(new BigDecimal("10"), cartServiceImpl.computeShippingCost(7));
        assertEquals(new BigDecimal("20"), cartServiceImpl.computeShippingCost(15));
        assertEquals(new BigDecimal("50"), cartServiceImpl.computeShippingCost(25));
    }


    @Test
    @DisplayName("Given an existing cart, " + "when clearing the cart, " + "then the cart products should be cleared successfully")
    void clearCartTest() {
        Long cartId = 1L;
        Cart cart = mock(Cart.class);
        cart.setId(cartId);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartServiceImpl.emptyCart(cartId);

        assertTrue(cart.getCartProducts().isEmpty());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Given a threshold date, " + "when identifying abandoned carts, " + "then return the list of abandoned carts")
    void identifyAbandonedCartsTest() {

        LocalDate thresholdDate = LocalDate.now().minusDays(2);
        List<Cart> abandonedCarts = new ArrayList<>();
        abandonedCarts.add(Cart.builder().id(1L).build());
        abandonedCarts.add(Cart.builder().id(2L).build());

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(abandonedCarts);

        List<CartDto> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertEquals(abandonedCarts.size(), result.size());
    }

    @Test
    @DisplayName("Given a threshold date, " + "when identifying abandoned carts, " + "then an empty list should be returned if there are no abandoned carts")
    void identifyAbandonedCarts_NoAbandonedCartsTest() {
        LocalDate thresholdDate = LocalDate.now().minusDays(2);

        when(cartRepository.identifyAbandonedCarts(thresholdDate)).thenReturn(Collections.emptyList());

        List<CartDto> result = cartServiceImpl.identifyAbandonedCarts(thresholdDate);

        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("Given a user id, " + "when creating a cart, " + "then a cart should be created successfully")
    void createCartTest() {
        Long userId = 123L;

        Cart cart = Cart.builder().id(userId).userId(userId).build();

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto createdCart = cartServiceImpl.createCart(userId);

        assertEquals(userId, createdCart.getUserId());
        assertEquals(userId, createdCart.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("Given a valid cart ID, when getting the cart, then return the corresponding cart and calculate the total correctly")
    void getCartTest() {
        Long cartId = 1L;
        Long userId = 1L;
        User user = User.builder().country(new Country(1L, 0.07)).id(userId).build();
        List<Product> products = new ArrayList<>();
        Product product = Product.builder().id(1L).price(new BigDecimal("20")).weight(2.0).currentStock(20).build();
        products.add(product);
        List<CartProduct> cartProducts = new ArrayList<>();
        CartProduct cartProduct = CartProduct.builder().productId(1L).price(new BigDecimal("20")).quantity(2).build();
        cartProducts.add(cartProduct);
        Cart cart = Cart.builder().id(cartId).userId(userId).cartProducts(Collections.singletonList(cartProduct)).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.findProductsByIds(anyList())).thenReturn(products);

        CartDto result = cartServiceImpl.fetchValidatedCart(cartId);

        assertEquals(cartId, result.getId());
    }

    @Test
    @DisplayName("Given a valid cart ID, when getting the cart if the amount exceeds the available amount, the method throws an exception")
    void getCartTestFail() {
        Long cartId = 1L;
        Long userId = 1L;
        User user = User.builder().country(new Country(1L, 0.07)).id(userId).build();
        List<Product> products = new ArrayList<>();
        Product product = Product.builder().id(1L).price(new BigDecimal("20")).weight(2.0).currentStock(20).build();
        products.add(product);
        List<CartProduct> cartProducts = new ArrayList<>();
        CartProduct cartProduct = CartProduct.builder().productId(1L).price(new BigDecimal("20")).quantity(200).build();
        cartProducts.add(cartProduct);
        Cart cart = Cart.builder().id(cartId).userId(userId).cartProducts(cartProducts).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productService.findProductsByIds(anyList())).thenReturn(products);

        assertThrows(CartProductInvalidQuantityException.class, () -> {
            cartServiceImpl.fetchValidatedCart(cartId);
        });
        verify(cartRepository).findById(cartId);
        verify(productService).findProductsByIds(anyList());
    }


    @Test
    @DisplayName("Given existing carts, " + "when retrieving all carts, " + "then return the list of all carts")
    void getAllCartsTest() {
        Cart cart1 = Cart.builder().build();
        Cart cart2 = Cart.builder().build();
        List<Cart> expectedCarts = Arrays.asList(cart1, cart2);

        when(cartRepository.findAll()).thenReturn(expectedCarts);

        List<Cart> actualCarts = cartServiceImpl.fetchAllCarts();

        assertEquals(expectedCarts.size(), actualCarts.size());
        assertTrue(actualCarts.contains(cart1));
        assertTrue(actualCarts.contains(cart2));
        verify(cartRepository).findAll();
    }


    @Test
    @DisplayName("Given a non-existent cart, " +
            "when adding a product to the cart, " +
            "then a CartNotFoundException should be thrown")
    void addProductToCart_CartNotFoundExceptionTest() {

        Cart cart = Cart.builder().id(1L).cartProducts(new ArrayList<>()).build();

        CartProduct cartProduct = CartProduct.builder().id(1L).productId(1L).quantity(10).cart(cart).build();

        when(productService.getProductById(anyLong())).thenReturn(new Product(1L, "prodName", "description", new BigDecimal("100"), 100, 100.0));
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));


        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartServiceImpl.addProductToCart(cartProduct));
    }

    @Test
    @DisplayName("Given a non-existent cart, " + "when clearing the cart, " + "then a CartNotFoundException should be thrown")
    void clearCart_CartNotFoundExceptionTest() {

        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartServiceImpl.emptyCart(1L));
    }

    @Test
    @DisplayName("Given a non-existent user, " + "when calculating the cart total, " + "then a UserNotFoundException should be thrown")
    void getCartTotal_UserNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User with ID " + userId + " not found"));

        assertThrows(UserNotFoundException.class, () -> cartServiceImpl.calculateCartTotal(cartId, userId));
    }


    @Test
    @DisplayName("Given a non-existent product in the cart, when calculating the cart total, then a CartProductNotFoundException should be thrown")
    void getCartTotal_CartProductNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;
        User user = User.builder().country(new Country(1L, 0.07)).id(userId).build();
        List<CartProduct> cartProducts = List.of(
                CartProduct.builder().productId(1L).quantity(2).price(new BigDecimal("10")).build()
        );
        Cart cart = Cart.builder().id(cartId).userId(userId).cartProducts(cartProducts).build();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(userService.getUserById(userId)).thenReturn(user);
        when(productService.getProductByIdWithDiscountedPrice(anyList())).thenThrow(new ExternalMicroserviceException("Product not found"));

        assertThrows(ExternalMicroserviceException.class, () -> cartServiceImpl.calculateCartTotal(cartId, userId));
    }


    @Test
    @DisplayName("Given a non-existent cart, " + "when calculating the cart total, " + "then a CartNotFoundException should be thrown")
    void getCartTotal_CartNotFoundExceptionTest() {
        Long cartId = 1L;
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(User.builder().build());
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartServiceImpl.calculateCartTotal(cartId, userId));
    }

    @Test
    @DisplayName("Given a user with an existing cart, " + "when creating a new cart, " + "then a UserWithCartException should be thrown")
    void createCart_UserWithCartExceptionTest() {
        Long userId = 123L;

        Cart existingCart = Cart.builder().userId(userId).build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existingCart));

        UserWithCartException exception = assertThrows(UserWithCartException.class, () -> cartServiceImpl.createCart(userId));

        assertEquals("User with ID " + userId + " already has a cart.", exception.getMessage());
    }

    @Test
    @DisplayName("Test updateExistingCartProduct")
    void testUpdateExistingCartProduct() {
        CartProduct existingCartProduct = CartProduct.builder().id(1L).productId(1L).quantity(5).build();
        CartProduct newCartProduct = CartProduct.builder().id(1L).productId(1L).quantity(10).build();

        cartServiceImpl.updateExistingCartProduct(existingCartProduct, newCartProduct);

        assertEquals(15, existingCartProduct.getQuantity());

        verify(cartProductRepository).save(existingCartProduct);
    }

    @Test
    @DisplayName("Given a list of cart products, when getIdList is called, then it should return the list of product IDs")
    void testGetIdList() {
        CartServiceImpl cartService = new CartServiceImpl(null, null, null, null);
        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setId(1L);
        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setId(2L);
        CartProduct cartProduct3 = new CartProduct();
        cartProduct3.setId(3L);

        List<CartProduct> cartProducts = List.of(cartProduct1, cartProduct2, cartProduct3);
        List<Long> result = getIdList(cartProducts);
        assertEquals(List.of(1L, 2L, 3L), result, "The IDs should match the IDs of the products in the list");
    }

    private List<Long> getIdList(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .map(CartProduct::getId)
                .toList();
    }

}
