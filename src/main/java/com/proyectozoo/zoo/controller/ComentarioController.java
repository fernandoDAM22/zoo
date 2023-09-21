package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.components.JWTUtil;
import com.proyectozoo.zoo.components.MessageComponent;
import com.proyectozoo.zoo.entity.Comentario;
import com.proyectozoo.zoo.service.IComentarioService;
import com.proyectozoo.zoo.util.Responses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/comentarios")
@Tag(name = "Comentarios", description = "Operaciones relacionadas con los comentarios")
public class ComentarioController {
    /**
     * Instancia del servicio
     */
    @Autowired
    private IComentarioService comentarioService;
    /**
     * Instancia de JWTUtil para acceder a la logica de jwt
     */
    @Autowired
    private JWTUtil jwtUtil;
    /**
     * Componente que permite manejar las validaciones y mostrar los mensajes de error correspondientes
     */
    @Autowired
    private ErrorUtils errorUtils;
    /**
     * Componente que nos permite tener acceso al fichero de mensajes
     */
    @Autowired
    private MessageComponent message;

    /**
     * Este metodo permite insertar un comentario en la base de datos
     *
     * @param token         es el token de autenticacion del usuario
     * @param comentario    es el comentario que queremos insertar en la base de datos
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha insertado correctamente el comentario o que ha ocurrido algun error
     */
    @PostMapping("/")
    @Operation(summary = "Guardar comentario", description = "Guarda un comentario en la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> guardar(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token, @Valid @RequestBody Comentario comentario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Long idUsuario = Long.parseLong(jwtUtil.getKey(token));
        comentario.setIdUsuario(idUsuario);
        if (!comentarioService.comentarioDisponible(comentario.getIdUsuario(), comentario.getIdAnimal())) {
            return Responses.badRequest(message.getMessage("error.comentario.limite"));
        }
        if (comentarioService.guardar(comentario) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.comentario.guardado"));
        }
        return Responses.badRequest(message.getMessage("error.comentario.guardar"));
    }

    /**
     * Este metodo permite borrar un comentario de la base da datos
     *
     * @param id    es el id del comenatario que queremos borrar
     * @param token es el token de autenticacion del usuario que esta intentando borrar el comentario
     * @return un ResponseEntity indicando que se ha borrado correctamente el comentario o a ocurrido algun error
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar comentario", description = "Borra un comentario en la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> borrarComentario(
            @Parameter(description = "Id del comentario que quermeos borrar") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Comentario dbComentario = comentarioService.obtenerPorId(id);
        if (dbComentario == null) {
            return Responses.notFound("error.comentario.id");
        }
        Long idUsuario = Long.parseLong(jwtUtil.getKey(token));
        if (!jwtUtil.validarAdmin(token) && !Objects.equals(dbComentario.getIdUsuario(), idUsuario)) {
            return Responses.badRequest("error.comentario.id_usuario");
        }
        if (comentarioService.borrar(id) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.comentario.borrado"));
        }
        return Responses.badRequest(message.getMessage("error.comentario.borrar"));
    }

    /**
     * Este metodo permite obtener todos los comentarios de la base de datos
     *
     * @param token es el token de autenticacion del usuario que esta intentando obtener los comentarios
     * @return una lista con todos los comentarios de la base da datos
     */
    @GetMapping("/")
    @Operation(summary = "Obtener comentarios", description = "Obtiene una lista con todos los comentarios de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Comentario>> obtenerComentarios(@Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(comentarioService.obtener());
    }

    /**
     * Este metodo permite obtener todos los comentarios de un animal
     *
     * @param token es el token de autenticacion del usuario
     * @param id    es el id del animal del que queremos obtener los comentarios
     * @return una lista con todos los comentarios del animal
     */
    @GetMapping("/animal/{id}")
    @Operation(summary = "Obtener comentarios de un animal", description = "Obtiene una lista con todos los comentarios de una animal")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Comentario>> obtenerComentarioPorAnimal(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Parameter(description = "id del animal del que queremos obtener los comentarios") @PathVariable Long id) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(comentarioService.obtenerComentariosPorAnimal(id));
    }
}