package com.proyectozoo.zoo.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ErrorUtils {


    @Autowired
    private MessageComponent message;

    public List<String> getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(this::resolveErrorMessage)
                .collect(Collectors.toList());
    }

    private String resolveErrorMessage(FieldError fieldError) {
        return message.getMessage(fieldError.getDefaultMessage());
    }
}

