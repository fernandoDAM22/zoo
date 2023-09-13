package com.proyectozoo.zoo.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Component
public class ValidationExceptionHandler {
    private final ErrorUtils errorUtils;

    @Autowired
    public ValidationExceptionHandler(ErrorUtils errorUtils) {
        this.errorUtils = errorUtils;
    }

    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<String> errores = errorUtils.getErrorMessages(bindingResult);
            return ResponseEntity.badRequest().body(errores);
        }

        // Resto del código de manejo de excepciones si es necesario
        return ResponseEntity.badRequest().body("Errores de validación desconocidos.");
    }
}
