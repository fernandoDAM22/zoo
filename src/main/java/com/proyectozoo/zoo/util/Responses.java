package com.proyectozoo.zoo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Responses {
    /**
     * ResponseEntity para devolver en caso de que el token de autenticacion no sea valido
     */
    public static ResponseEntity<String> FORBIDDEN = ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body("Token de autenticacion invalido");

    /**
     * Este metodo devuelve un ResponseEntity con un StatusCode 400
     *
     * @param mensaje es el mensaje que se anade al cuerpo del ResposeEntity
     * @return el ResponseEntity creado
     */
    public static ResponseEntity<String> badRequest(String mensaje) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
    }

    /**
     * Este metodo devuelve un ResponseEntity con un StatusCode 409
     *
     * @param mensaje es el mensaje que se anade al cuerpo del ResposeEntity
     * @return el ResponseEntity creado
     */
    public static ResponseEntity<String> conflict(String mensaje) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(mensaje);
    }
    /**
     * Este metodo devuelve un ResponseEntity con un StatusCode 401
     *
     * @param mensaje es el mensaje que se anade al cuerpo del ResposeEntity
     * @return el ResponseEntity creado
     */
    public static ResponseEntity<String> created(String mensaje) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }
    /**
     * Este metodo devuelve un ResponseEntity con un StatusCode 404
     *
     * @param mensaje es el mensaje que se anade al cuerpo del ResposeEntity
     * @return el ResponseEntity creado
     */
    public static ResponseEntity<String> notFound(String mensaje) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensaje);
    }
}
