package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.services.CartService;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {

    private CartServiceImpl cartServiceImpl;

    public CartController(CartServiceImpl cartServiceImpl) {
        this.cartServiceImpl = cartServiceImpl;
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        return null;
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<Cart> removeCartById(@PathVariable Long id) {
        return null;
    }

    @PostMapping("/cartProducts")
    public ResponseEntity<Cart> addProduct() {
        return null;
    }

    @PatchMapping("/cartProducts")
    public ResponseEntity<Cart> modifyProduct() {
        return null;
    }

    @DeleteMapping("/cartProducts/{id}")
    public ResponseEntity<Cart> removeProductByIdTest(@PathVariable Long id) {
        return null;
    }
}
