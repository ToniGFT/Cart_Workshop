package com.gftworkshop.cartmicroservice.exceptions;

public class UserWithCartException extends RuntimeException {

    public UserWithCartException(String message) {
        super(message);
    }
}
