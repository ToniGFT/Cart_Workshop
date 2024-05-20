package com.gftworkshop.cartmicroservice.exceptions;

public class CartProductNotFoundException extends RuntimeException{

    public CartProductNotFoundException(String message) {
        super(message);
    }
}
