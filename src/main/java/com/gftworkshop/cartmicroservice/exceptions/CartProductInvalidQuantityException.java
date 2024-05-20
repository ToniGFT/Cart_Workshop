package com.gftworkshop.cartmicroservice.exceptions;

public class CartProductInvalidQuantityException extends RuntimeException {
    public CartProductInvalidQuantityException(String message) {
        super(message);
    }
}
