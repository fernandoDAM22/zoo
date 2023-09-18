package com.proyectozoo.zoo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class Responses {
    /**
     * Este metodo devuelve un ResponseEntity con un StatusCode 403
     *
     * @param mensaje es el mensaje que se anade al cuerpo del ResposeEntity
     * @return el ResponseEntity creado
     */
    public static ResponseEntity<String> forbidden(String mensaje) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
    }

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
