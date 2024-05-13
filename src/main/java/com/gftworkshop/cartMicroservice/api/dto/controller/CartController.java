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
    public ResponseEntity<Cart> addCartById(@RequestBody Cart cart) {
        Cart savedCart = cartService.createCart(cart.getId());
        if (savedCart != null) {
            return ResponseEntity.ok(savedCart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Cart recievedCart = cartService.getCart(id);
        if(recievedCart != null){
            return ResponseEntity.ok(recievedCart);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<Cart> removeCartById(@PathVariable Long id) {
        cartService.clearCart(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/carts/products")
    public ResponseEntity<Cart> addProduct(@RequestBody CartProduct cartProduct) {
        cartProductService.save(cartProduct);
        cartService.addProductToCart(cartProduct);
        return ResponseEntity.ok().build();
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
