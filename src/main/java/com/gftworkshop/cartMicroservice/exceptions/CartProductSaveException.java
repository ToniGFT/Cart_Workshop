package com.gftworkshop.cartMicroservice.exceptions;

public class CartProductSaveException extends RuntimeException {
    public CartProductSaveException(String message) {
        super(message);
    }
}
