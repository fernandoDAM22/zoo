package com.proyectozoo.zoo.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.Set;

@RestControllerAdvice
public class CustomHandler {
    /**
     * Este metodo permite manejar las excepciones MissingRequestHeader
     * @param sqlException es la excepcion que ha ocurrido
     * @return un ResponseEntity indicando el mensaje de error y con un status code 400
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleMissingRequestHeader(SQLException sqlException) {
        return Responses.badRequest(sqlException.getMessage());
    }
    /**
     * Este metodo permite manejar las excepciones ConstraintViolationException
     * @param constraintViolationException es la excepcion que ha ocurrido
     * @return un ResponseEntity indicando el mensaje de error y con un status code 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException constraintViolationException) {
        Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
        String errorMessage = "";
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            violations.forEach(violation -> builder.append(" " + violation.getMessage() + "\n"));
            errorMessage = builder.toString();
        } else {
            errorMessage = "ConstraintViolationException occured.";
        }
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
    /**
     * Este metodo permite manejar las excepciones ExpiredJwtException
     * @param expiredJwtException es la excepcion que ha ocurrido
     * @return un ResponseEntity indicando el mensaje de error y con un status code 400
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String>  handleExpiredJwtException(ExpiredJwtException expiredJwtException){
        return Responses.badRequest("El token de autenticacion ha expirado");
    }

}
