package com.yadro.pcbdispatcher.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlePCBNotFoundException() {
        PCBNotFoundException ex = new PCBNotFoundException(1L);
        ResponseEntity<ErrorResponse> response = handler.handlePCBNotFoundException(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("PCB не найдена: id=1", body.getMessage());
    }

    @Test
    void handleIllegalStateException() {
        IllegalStateException ex = new IllegalStateException("Тестовая ошибка");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Тестовая ошибка", body.getMessage());
    }
} 