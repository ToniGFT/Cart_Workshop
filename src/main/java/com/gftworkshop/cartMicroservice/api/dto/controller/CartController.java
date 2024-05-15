package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.CartProductNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.ErrorResponse;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<?> addCartByUserId(@PathVariable("id") String id) {
        try {
            Long idCart = Long.parseLong(id);
            Cart createdCart = cartService.createCart(idCart);
            if (createdCart != null) {
                return ResponseEntity.created(URI.create("/carts/" + createdCart.getId())).body(createdCart);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (NumberFormatException e) {
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (CartNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/carts/{id}")
    public ResponseEntity<?> getCartById(@PathVariable("id") String id) {
        try {
            Long idCart = Long.parseLong(id);
            Cart receivedCart = cartService.getCart(idCart);
            return ResponseEntity.ok(receivedCart);
        } catch (NumberFormatException e) {
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (CartNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<?> removeCartById(@PathVariable("id") String id) {
        try {
            Long idCart = Long.parseLong(id);
            cartService.clearCart(idCart);
            return ResponseEntity.ok().build();
        }catch (NumberFormatException e){
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch (CartNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/carts/products")
    public ResponseEntity<?> addProduct(@RequestBody CartProduct cartProduct) {
        try{
            cartService.addProductToCart(cartProduct);
            return ResponseEntity.ok().build();
        }catch (NumberFormatException e){
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PatchMapping("/carts/products")
    public ResponseEntity<?> updateProduct(@RequestBody CartProduct cartProduct) {
        try{
            cartProductService.updateQuantity(cartProduct.getId(), cartProduct.getQuantity());
            return ResponseEntity.ok().build();
        }catch (NumberFormatException e){
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    @DeleteMapping("/carts/products/{id}")
    public ResponseEntity<?> removeProductById(@PathVariable("id") Long id) {
        try{
            CartProduct deletedCartProduct = cartProductService.removeProduct(id);
            return ResponseEntity.ok(deletedCartProduct);
        } catch (NumberFormatException e) {
            ErrorResponse errorResponse = new ErrorResponse(400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }catch(CartProductNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
