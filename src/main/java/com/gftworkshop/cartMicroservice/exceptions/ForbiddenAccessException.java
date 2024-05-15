package com.gftworkshop.cartMicroservice.exceptions;

public class ForbiddenAccessException extends RuntimeException{

    public ForbiddenAccessException(String message) {
        super(message);
    }
}
