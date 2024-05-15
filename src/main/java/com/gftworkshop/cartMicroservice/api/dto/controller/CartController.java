package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CartController {

    private CartServiceImpl cartService;
    private CartProductServiceImpl cartProductService;

    public CartController(CartServiceImpl cartService, CartProductServiceImpl cartProductService) {
        this.cartService = cartService;
        this.cartProductService = cartProductService;
    }

    @GetMapping("/carts")
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> savedCart = cartService.getAllCarts();
        return ResponseEntity.ok(savedCart);
    }

    @PostMapping("/carts/{id}")
    public ResponseEntity<Cart> addCartByUserId(@PathVariable("id") Long id) {
        Optional<Cart> optionalCart = Optional.ofNullable(cartService.createCart(id));
        return optionalCart.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable("id") Long id) {
        try {
            Cart receivedCart = cartService.getCart(id);
            return ResponseEntity.ok(receivedCart);
        } catch (CartNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<Cart> removeCartById(@PathVariable("id") Long id) {
        cartService.clearCart(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/carts/products")
    public ResponseEntity<Cart> addProduct(@RequestBody CartProduct cartProduct) {
        cartService.addProductToCart(cartProduct);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/carts/products")
    public ResponseEntity<Cart> updateProduct(@RequestBody CartProduct cartProduct) {
        cartProductService.updateQuantity(cartProduct.getId(), cartProduct.getQuantity());
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/carts/products/{id}")
    public ResponseEntity<CartProduct> removeProductById(@PathVariable("id") Long id) {
        try{
            CartProduct deletedCartProduct = cartProductService.removeProduct(id);
            return ResponseEntity.ok(deletedCartProduct);
        } catch(CartProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
