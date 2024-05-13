package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {

    private CartServiceImpl cartService;
    private CartProductServiceImpl cartProductService;

    public CartController(CartServiceImpl cartService, CartProductServiceImpl cartProductService) {
        this.cartService = cartService;
        this.cartProductService = cartProductService;
    }

    @PostMapping("/carts/{id}")
    public ResponseEntity<Cart> addCartById(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        return null;
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<Cart> removeCartById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/carts/products")
    public ResponseEntity<Cart> addProduct(@RequestBody CartProduct cartProduct) {
        cartProductService.save(cartProduct);
        //cartService.addProductToCart(cartProduct.getCart().getId(), cartProduct);
        return null;
    }

    @PatchMapping("/carts/products")
    public ResponseEntity<Cart> updateProduct(@RequestBody CartProduct cartProduct) {
        cartProductService.updateQuantity(cartProduct.getId(), cartProduct.getQuantity());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/carts/products/{id}")
    public ResponseEntity<Cart> removeProductById(@PathVariable Long id) {
        cartProductService.removeProduct(id);
        return ResponseEntity.ok().build();
    }
}
