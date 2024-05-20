package com.gftworkshop.cartMicroservice.exceptions;

import org.springframework.http.HttpStatusCode;

public class ExternalMicroserviceException extends RuntimeException {

    private HttpStatusCode statusCode = null;

    public ExternalMicroserviceException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ExternalMicroserviceException(String message) {
        super(message);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
