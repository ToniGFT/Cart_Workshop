package com.gftworkshop.cartmicroservice.api.dto.controller;

import com.gftworkshop.cartmicroservice.api.dto.CartDto;
import com.gftworkshop.cartmicroservice.api.dto.UpdatedCartProductDto;
import com.gftworkshop.cartmicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartmicroservice.model.Cart;
import com.gftworkshop.cartmicroservice.model.CartProduct;
import com.gftworkshop.cartmicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartmicroservice.services.impl.CartServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class CartControllerTest {

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
            CartDto savedCart = new CartDto();
            savedCart.setId(cartId);

            when(cartService.createCart(cartId)).thenReturn(savedCart);

            mockMvc.perform(post("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("When getting cart by ID, then expect OK status")
        void getCartByIdTest() throws Exception {
            CartDto cart = new CartDto();
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
            CartProductDto cartProduct = new CartProductDto();
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
        @DisplayName("When adding product, then expect OK status")
        void addProductTest() {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setId(1L);
            cartProduct.setProductName("Product Name");
            cartProduct.setProductCategory("Product Category");
            cartProduct.setProductDescription("Product Description");
            cartProduct.setQuantity(1);
            cartProduct.setPrice(BigDecimal.TEN);

            ResponseEntity<?> response = cartController.addProduct(cartProduct);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(cartService, times(1)).addProductToCart(cartProduct);
        }


        @Test
        @DisplayName("When adding cart by ID, then expect CREATED status")
        void addCartByIdTest() {
            CartDto cart = new CartDto();
            cart.setId(cartId);
            when(cartService.createCart(1L)).thenReturn(cart);

            ResponseEntity<?> response = cartController.addCartByUserId(String.valueOf(cartId));

            assertEquals(HttpStatus.CREATED, response.getStatusCode());

            assertTrue(response.getHeaders().containsKey("Location"));
            assertEquals("/carts/" + cartId, response.getHeaders().getFirst("Location"));

            assertEquals(cart, response.getBody());
        }

        @Test
        @DisplayName("When adding cart by ID and cart creation fails, then expect Not Found status")
        void addCartByIdFailureTest() throws Exception {
            when(cartService.createCart(cartId)).thenReturn(null);

            mockMvc.perform(post("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("When getting cart by ID, then expect OK status")
        void getCartByIdTest() {
            CartDto cart = new CartDto();
            cart.setId(cartId);

            when(cartService.getCart(cartId)).thenReturn(cart);

            ResponseEntity<?> response = cartController.getCartById(String.valueOf(cart.getId()));

            verify(cartService, times(1)).getCart(cartId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When getting cart by ID and cart retrieval fails, then expect Not Found status")
        void getCartByIdFailureTest() throws Exception {
            when(cartService.getCart(cartId)).thenReturn(null);

            mockMvc.perform(get("/carts/{id}", cartId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("When removing cart by ID, then expect OK status")
        void removeCartByIdTest() {
            ;
            doNothing().when(cartService).clearCart(cartId);

            ResponseEntity<?> response = cartController.removeCartById(String.valueOf(cartId));

            verify(cartService, times(1)).clearCart(cartId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When removing product by ID and product deletion fails, then expect Not Found status")
        void removeProductByIdFailureTest() throws Exception {
            when(cartProductService.removeProduct(productId)).thenReturn(null);

            mockMvc.perform(delete("/carts/products/{id}", productId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    @DisplayName("Tests for CartProducts operations")
    class CartProductOperationsTests {

        @Test
        @DisplayName("When updating a product, then expect OK status")
        void testUpdateProduct() {
            int newQuantity = 5;
            UpdatedCartProductDto cartProduct = new UpdatedCartProductDto();
            cartProduct.setId(productId);
            cartProduct.setQuantity(newQuantity);

            when(cartProductService.updateQuantity(productId, newQuantity)).thenReturn(ResponseEntity.ok().build().getStatusCode().value());

            ResponseEntity<?> response = cartController.updateProduct(cartProduct);

            verify(cartProductService, times(1)).updateQuantity(productId, newQuantity);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("When removing a product, then expect OK status")
        void removeProductByIdTest() throws Exception {

            CartProductDto cartProduct = new CartProductDto();
            Cart cart = new Cart();
            cart.setId(cartId);

            when(cartProductService.removeProduct(anyLong())).thenReturn(cartProduct);

            ResponseEntity<?> response = cartController.removeProductById(String.valueOf(productId));

            assertEquals(ResponseEntity.ok(cartProduct), response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    @DisplayName("When getting all carts and carts exist, then expect OK status")
    void getAllCartsExistsTest() {
        List<Cart> carts = new ArrayList<>();
        Cart cart1 = new Cart();
        cart1.setId(1L);
        Cart cart2 = new Cart();
        cart2.setId(2L);
        carts.add(cart1);
        carts.add(cart2);
        when(cartService.getAllCarts()).thenReturn(carts);

        ResponseEntity<List<Cart>> response = cartController.getAllCarts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(carts, response.getBody());
        verify(cartService, times(1)).getAllCarts();
    }
}