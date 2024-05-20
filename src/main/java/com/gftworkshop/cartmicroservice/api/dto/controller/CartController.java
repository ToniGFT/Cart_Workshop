package com.gftworkshop.cartmicroservice.api.dto.controller;

import com.gftworkshop.cartmicroservice.api.dto.CartDto;
import com.gftworkshop.cartmicroservice.api.dto.UpdatedCartProductDto;
import com.gftworkshop.cartmicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartmicroservice.model.Cart;
import com.gftworkshop.cartmicroservice.model.CartProduct;
import com.gftworkshop.cartmicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartmicroservice.services.impl.CartServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Validated
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
    public ResponseEntity<?> addCartByUserId(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        CartDto createdCart = cartService.createCart(idCart);
        if (createdCart != null)
            return ResponseEntity.created(URI.create("/carts/" + createdCart.getId())).body(createdCart);
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/carts/{id}")
    public ResponseEntity<?> getCartById(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        CartDto receivedCart = cartService.getCart(idCart);
        if (receivedCart != null)
            return ResponseEntity.ok(receivedCart);
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<?> removeCartById(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        cartService.clearCart(idCart);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/carts/products")
    public ResponseEntity<?> addProduct(@Valid @RequestBody CartProduct cartProduct) {
        cartService.addProductToCart(cartProduct);
        return ResponseEntity.ok().build();

    }

    @PatchMapping("/carts/products")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody UpdatedCartProductDto cartProduct) {
        cartProductService.updateQuantity(cartProduct.getId(), cartProduct.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/carts/products/{id}")
    public ResponseEntity<?> removeProductById(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        CartProductDto deletedCartProduct = cartProductService.removeProduct(idCart);
        if (deletedCartProduct != null)
            return ResponseEntity.ok(deletedCartProduct);
        return ResponseEntity.notFound().build();
    }
}
