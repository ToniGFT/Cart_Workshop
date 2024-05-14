package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CartControllerTest {

    private MockMvc mockMvc;
    private CartController cartController;
    private CartProductServiceImpl cartProductService;
    private CartServiceImpl cartService;
    private Long cartId;
    private Long productId;
    private String requestBodyCartProduct;
    private String requestBodyCart;

    @BeforeEach
    void setUp() {
        cartService = mock(CartServiceImpl.class);
        cartProductService = mock(CartProductServiceImpl.class);
        cartController = new CartController(cartService, cartProductService);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        cartId = 1L;
        productId = 1L;
        requestBodyCart = "{"
                + "\"id\": null,"
                + "\"user_id\": 123,"
                + "\"updated_at\": \"2024-05-14T12:00:00\","
                + "\"cartProducts\": ["
                + "{\"id\": null, \"productName\": \"Product 1\", \"productCategory\": \"Category 1\", \"productDescription\": \"Description 1\", \"quantity\": 1, \"price\": 10.50},"
                + "{\"id\": null, \"productName\": \"Product 2\", \"productCategory\": \"Category 2\", \"productDescription\": \"Description 2\", \"quantity\": 2, \"price\": 20.50}"
                + "]"
                + "}";

        requestBodyCartProduct = "{"
                + "\"id\": null,"
                + "\"cart\": {\"id\": 1},"
                + "\"productName\": \"product name\","
                + "\"productCategory\": \"product category\","
                + "\"productDescription\": \"product description\","
                + "\"quantity\": 5,"
                + "\"price\": 10.50"
                + "}";
    }

    @Nested
    @DisplayName("Tests for Cart code status")
    class CartCodeStatusTests {

        @Test
        @DisplayName("When adding cart by ID, then expect OK status")
        void addCartByIdTest() throws Exception {
            Cart savedCart = new Cart();
            savedCart.setId(cartId);

            when(cartService.createCart(cartId)).thenReturn(savedCart);

            mockMvc.perform(post("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When getting cart by ID, then expect OK status")
        void getCartByIdTest() throws Exception {
            Cart cart = new Cart();
            cart.setId(cartId);

            when(cartService.getCart(cartId)).thenReturn(cart);

            mockMvc.perform(get("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When removing cart by ID, then expect OK status")
        void removeCartByIdTest() throws Exception {
            mockMvc.perform(delete("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for CartProduct code status")
    class CartProductCodeStatusTests {

        @Test
        @DisplayName("When adding product, then expect OK status")
        void addProductTest() throws Exception {
            mockMvc.perform(patch("/carts/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBodyCartProduct))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When updating product, then expect OK status")
        void updateProductTest() throws Exception {
            mockMvc.perform(patch("/carts/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBodyCartProduct))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("When removing product by ID, then expect OK status")
        void removeProductByIdTest() throws Exception {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setId(productId);

            when(cartProductService.removeProduct(anyLong())).thenReturn(cartProduct);

            mockMvc.perform(delete("/carts/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for Cart operations")
    class CartOperationsTests {

        @Test
        @DisplayName("When adding cart by ID, then expect OK status")
        void addCartByIdTest() {
            Cart cart = new Cart();
            cart.setId(cartId);

            when(cartService.createCart(1L)).thenReturn(cart);

            ResponseEntity<Cart> response = cartController.addCartById(cartId);

            assertEquals(ResponseEntity.ok(cart), response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When getting cart by ID, then expect OK status")
        void getCartByIdTest() {
            Cart cart = new Cart();
            cart.setId(cartId);

            when(cartService.getCart(cartId)).thenReturn(cart);

            ResponseEntity<Cart> response = cartController.getCartById(cart.getId());

            verify(cartService, times(1)).getCart(cartId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When removing cart by ID, then expect OK status")
        void removeCartByIdTest(){;
            doNothing().when(cartService).clearCart(cartId);

            ResponseEntity<Cart> response = cartController.removeCartById(cartId);

            verify(cartService, times(1)).clearCart(cartId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Tests for Cart and CartProducts operations")
    class CartAndCartProductOperationsTests {
        @Test
        @DisplayName("When adding product, then expect OK status")
        void addProductTest() {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setId(1L);
            cartProduct.setProductName("Product Name");
            cartProduct.setProductCategory("Product Category");
            cartProduct.setProductDescription("Product Description");
            cartProduct.setQuantity(1);
            cartProduct.setPrice(BigDecimal.TEN);

            when(cartProductService.save(cartProduct)).thenReturn(cartProduct);
            doNothing().when(cartService).addProductToCart(cartProduct);

            ResponseEntity<Cart> response = cartController.addProduct(cartProduct);

            verify(cartProductService, times(1)).save(cartProduct);
            verify(cartService, times(1)).addProductToCart(cartProduct);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Tests for CartProducts operations")
    class CartProductOperationsTests {

        @Test
        @DisplayName("When updating a product, then expect OK status")
        void testUpdateProduct() {
            int newQuantity = 5;
            CartProduct cartProduct = new CartProduct();
            cartProduct.setId(productId);
            cartProduct.setQuantity(newQuantity);

            when(cartProductService.updateQuantity(productId, newQuantity)).thenReturn(ResponseEntity.ok().build().getStatusCode().value());

            ResponseEntity<Cart> response = cartController.updateProduct(cartProduct);

            verify(cartProductService, times(1)).updateQuantity(productId, newQuantity);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When removing a product, then expect OK status")
        void removeProductByIdTest() throws Exception {
            CartProduct cartProduct = new CartProduct();
            Cart cart = new Cart();
            cart.setId(cartId);
            cartProduct.setCart(cart);

            when(cartProductService.removeProduct(anyLong())).thenReturn(cartProduct);

            ResponseEntity<CartProduct> response = cartController.removeProductById(productId);

            assertEquals(ResponseEntity.ok(cartProduct), response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}
