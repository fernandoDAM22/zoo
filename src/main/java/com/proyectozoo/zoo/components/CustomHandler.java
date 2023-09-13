package com.proyectozoo.zoo.components;

import com.proyectozoo.zoo.util.Responses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class CustomHandler {
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleMissingRequestHeader(SQLException ex) {
        return Responses.badRequest(ex.getMessage());
    }


}
