package com.gftworkshop.cartMicroservice.exceptions;

import com.gftworkshop.cartMicroservice.exceptions.CartNotFoundException;
import com.gftworkshop.cartMicroservice.exceptions.ErrorResponse;
import com.gftworkshop.cartMicroservice.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleNumberFormatException() {
        NumberFormatException exception = new NumberFormatException("Invalid number format");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleNumberFormatException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Invalid number format", responseEntity.getBody().getMessage());
    }

    @Test
    void testHandleCartNotFoundException() {
        CartNotFoundException exception = new CartNotFoundException("Cart not found");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleCartNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Cart not found", responseEntity.getBody().getMessage());
    }

    @Test
    void testHandleGeneralException() {
        InternalError exception = new InternalError("Internal server error");
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleGeneralException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Internal server error", responseEntity.getBody().getMessage());
    }
}
