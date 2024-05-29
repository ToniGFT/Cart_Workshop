package com.gftworkshop.cartMicroservice.services.impl;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.Country;
import com.gftworkshop.cartMicroservice.api.dto.Product;
import com.gftworkshop.cartMicroservice.api.dto.User;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.repositories.CartProductRepository;
import com.gftworkshop.cartMicroservice.repositories.CartRepository;
import com.gftworkshop.cartMicroservice.services.ProductService;
import com.gftworkshop.cartMicroservice.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceImplIntegrationTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    private CartServiceImpl cartService;

    @BeforeEach
    public void setUp() {
        cartService = new CartServiceImpl(cartRepository, cartProductRepository, productService, userService);
    }

    @Test
    @Transactional
    void testAddProductToCart() {
        Cart cart = Cart.builder()
                .userId(1L)
                .updatedAt(LocalDate.now())
                .cartProducts(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.0))
                .currentStock(10)
                .weight(1.0)
                .build();

        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .productId(product.getId())
                .quantity(2)
                .price(product.getPrice())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .build();

        cartService.addProductToCart(cartProduct);

        Optional<Cart> updatedCart = cartRepository.findById(cart.getId());
        assertTrue(updatedCart.isPresent());
        assertEquals(1, updatedCart.get().getCartProducts().size());
        assertEquals(2, updatedCart.get().getCartProducts().get(0).getQuantity());
    }

    @Test
    @Transactional
    void testCalculateCartTotal() {

        Cart cart = Cart.builder()
                .userId(1L)
                .updatedAt(LocalDate.now())
                .cartProducts(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.0))
                .currentStock(10)
                .weight(1.0)
                .build();

        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .productId(product.getId())
                .quantity(2)
                .price(product.getPrice())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .build();
        cartProduct = cartProductRepository.save(cartProduct);

        cart.getCartProducts().add(cartProduct);
        cart = cartRepository.save(cart);

        Country country = Country.builder()
                .id(1L)
                .tax(20.0)
                .build();
        User user = User.builder()
                .id(1L)
                .country(country)
                .build();

        BigDecimal total = cartService.calculateCartTotal(cart.getId(), user.getId());

        BigDecimal expectedTotal = new BigDecimal("1222.3958");

        assertEquals(expectedTotal, total);
    }


    @Test
    @Transactional
    void testEmptyCart() {
        Cart cart = Cart.builder()
                .userId(1L)
                .updatedAt(LocalDate.now())
                .cartProducts(new ArrayList<>())
                .build();
        cart = cartRepository.save(cart);

        Product product = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.0))
                .currentStock(10)
                .weight(1.0)
                .build();

        CartProduct cartProduct = CartProduct.builder()
                .cart(cart)
                .productId(product.getId())
                .quantity(2)
                .price(product.getPrice())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .build();
        cartProduct = cartProductRepository.save(cartProduct);

        cart.getCartProducts().add(cartProduct);
        cart = cartRepository.save(cart);

        cartService.emptyCart(cart.getId());

        Optional<Cart> updatedCart = cartRepository.findById(cart.getId());
        assertTrue(updatedCart.isPresent());
        assertEquals(0, updatedCart.get().getCartProducts().size());
    }

    @Test
    @Transactional
    void testIdentifyAbandonedCarts() {

        Cart cart1 = Cart.builder()
                .userId(1L)
                .updatedAt(LocalDate.now().minusDays(3))
                .cartProducts(new ArrayList<>())
                .build();
        cart1 = cartRepository.save(cart1);

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(100.0))
                .currentStock(10)
                .weight(1.0)
                .build();

        CartProduct cartProduct1 = CartProduct.builder()
                .id(1L)
                .cart(cart1)
                .productId(product1.getId())
                .quantity(2)
                .price(product1.getPrice())
                .productName(product1.getName())
                .productDescription(product1.getDescription())
                .build();
        cartProduct1 = cartProductRepository.save(cartProduct1);

        cart1.getCartProducts().add(cartProduct1);
        cartRepository.save(cart1);

        List<CartDto> expectedAbandonedCarts = new ArrayList<>();

        expectedAbandonedCarts.add(EntityToDto.convertCartToDto(cart1));

        LocalDate thresholdDate = LocalDate.now().minusDays(2);

        List<CartDto> abandonedCarts = cartService.identifyAbandonedCarts(thresholdDate);
        abandonedCarts = abandonedCarts.stream()
                .filter(c -> c.getId().equals(expectedAbandonedCarts.get(0).getId()))
                .toList();

        assertEquals(expectedAbandonedCarts, abandonedCarts);

    }

    @Test
    void testCreateCart() {

        Long userId = 10L;

        CartDto createdCart = cartService.createCart(userId);

        assertNotNull(createdCart);
        assertNotNull(createdCart.getId());
        assertEquals(userId, createdCart.getUserId());

        Cart fetchedCart = cartRepository.findById(createdCart.getId()).orElse(null);
        assertNotNull(fetchedCart);
        assertEquals(userId, fetchedCart.getUserId());
    }

    @Test
    @Transactional
    void testFetchValidatedCart() {

        Long cartId = 1L;
        Long userId = 1L;

        CartDto fetchedCartDto = cartService.fetchValidatedCart(cartId);

        BigDecimal expectedTotalPrice = BigDecimal.valueOf(100);

        BigDecimal totalPrice = BigDecimal.valueOf(100).setScale(0, RoundingMode.HALF_UP);

        assertNotNull(fetchedCartDto);
        assertEquals(cartId, fetchedCartDto.getId());
        assertEquals(userId, fetchedCartDto.getUserId());
        assertEquals(expectedTotalPrice, totalPrice);
    }


}
