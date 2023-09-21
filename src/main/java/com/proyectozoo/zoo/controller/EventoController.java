package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.components.JWTUtil;
import com.proyectozoo.zoo.components.MessageComponent;
import com.proyectozoo.zoo.entity.Evento;
import com.proyectozoo.zoo.service.IEventoService;
import com.proyectozoo.zoo.service.IUploadFileService;
import com.proyectozoo.zoo.util.Responses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


@RestController
@RequestMapping("api/eventos")
@Tag(name = "Animales", description = "Operaciones relacionadas con los eventos")
public class EventoController {
    /**
     * Instancia del servicio
     */
    @Autowired
    private IEventoService service;
    /**
     * Instancia de JWTUtil para acceder a la logica de jwt
     */
    @Autowired
    private JWTUtil jwtUtil;
    /**
     * Instancia del servicio que nos permite subir archivos al servidor
     */
    @Autowired
    private IUploadFileService uploadFileService;
    /**
     * Ruta a la carpeta donde se guardan las imagenes
     */
    @Value("${ruta.imagenes.eventos}")
    private String carpetaImagenes;
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
     * Este metodo permite obtener todos los eventos de la base de datos
     *
     * @param token es el token de autenticacion del usuario que esta intentanco obtener los eventos
     * @return una lista con todos los eventos, o un error
     */
    @GetMapping("/")
    @Operation(summary = "Obtener eventos", description = "Obtiene una lista con todos los eventos de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Evento>> obtener(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(service.obtener());
    }

    /**
     * Este metodo permite obtener todos los eventos de una seccion
     *
     * @param id    es el id de la seccion de la que queremos obtener todos los eventos
     * @param token es el token de autenticacion del usuario
     * @return una lista con todos los eventos de la seccion
     */
    @GetMapping("/seccion/{id}")
    @Operation(summary = "Obtener eventos por seccion", description = "Obtiene una lista con todos los eventos de una seccion")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Evento>> obtenerEventosPorSeccion(
            @Parameter(description = "Id de la seccion de la que queremos obtener los eventos") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(service.obtenerEventosPorSeccion(id));
    }

    /**
     * Este metodo permite obtener un evento por su id
     *
     * @param id    es el id del evento que queremos obtener
     * @param token es el token de autenticacion del usuario
     * @return un ResponseEntity con el evento con el id indicado o un error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un evento", description = "Obtiene un evento a partir de su id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Evento> obtenerEvento(
            @Parameter(description = "Id del evento que queremos obtener") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        Evento evento = service.buscarPorId(id);
        if (evento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error", message.getMessage("error.evento.id")).body(null);
        }
        return ResponseEntity.ok(evento);
    }

    /**
     * Este metodo permite insertar un evento
     *
     * @param token         es el token de autenticacion del usuario
     * @param evento        es el evento que queremos insertar
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha insertado correctamente el evento o que ha ocurrido algun error
     */
    @PostMapping("/")
    @Operation(summary = "Insertar evento", description = "Inserta un evento en la base de datos")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> insertar(@Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
                                           @Valid @RequestBody Evento evento, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        if (service.buscarPorNombre(evento.getNombre()) != null) {
            return Responses.badRequest(message.getMessage("error.evento.nombre_repetido"));
        }
        evento.setFoto("C://imagenes//zoo//eventos//default.png");
        if (service.guardar(evento) != null) {
            return ResponseEntity.ok(String.valueOf(evento.getId()));
        }
        return Responses.badRequest(message.getMessage("error.evento.guardar"));
    }

    /**
     * Este metodo permite asignar una imagen a un evento
     *
     * @param id    es el id del evento al que le queremos asignar la imagen
     * @param token es el token de autenticacion del usuario
     * @param file  es la imagen que le queremos asignar al evento
     * @return un ResponseEntity con la ruta de la imagen asignada al evento o un error
     */
    @PostMapping("/imagen/{id}")
    @Operation(summary = "Subir imagen", description = "Sube una imagen al servidor y se la asigna a un evento")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> subir(
            @Parameter(description = "Id del evento al que le queremos asignar la imagen") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Parameter(description = "Imagen que le queremos asignar al evento") @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Evento evento = service.buscarPorId(id);
        if (evento == null) {
            return Responses.badRequest(message.getMessage("error.evento.nombre"));
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);

        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }
        evento.setFoto(path);
        service.guardar(evento);

        return ResponseEntity.ok(path);
    }

    /**
     * Este metodo permite borrar un evento
     *
     * @param token es el token de autenticacion del usuario
     * @param id    es el id del evento que queremos borrar
     * @return un ResponseEntity indicando que se ha borrado correctamente el evento o que ha ocurrido algun error
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar evento", description = "Borra un evento de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> borrar(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Parameter(description = "Id del evento") @PathVariable Long id) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Evento dbEvento = service.buscarPorId(id);
        if (dbEvento == null) {
            return Responses.badRequest(message.getMessage("error.evento.id"));
        }
        File file = new File(dbEvento.getFoto());
        file.delete();
        if (service.borrar(id) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.evento.borrado"));
        }
        return Responses.badRequest(message.getMessage("error.evento.borrar"));
    }

    /**
     * Este metodo permite modificar un evento
     *
     * @param token         es el token de autenticacion del usuario que esta intentando modificar el evento
     * @param evento        es el evento que queremos modificar
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha modificado el evento correctamente o que ha ocurrido algun error
     */
    @PutMapping("/")
    @Operation(summary = "Modificar evento", description = "Modifica un evento a excepcion de su foto")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> modificarEvento(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Valid @RequestBody Evento evento, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Evento dbEvento = service.buscarPorId(evento.getId());
        if (dbEvento == null) {
            return Responses.notFound(message.getMessage("error.evento.id"));
        }
        if (service.buscarPorNombre(evento.getNombre()) != null && !evento.getNombre().equals(dbEvento.getNombre())) {
            return Responses.conflict(message.getMessage("error.evento.nombre_repetido"));
        }
        if (service.modificar(evento) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.evento.actualizado"));
        }
        return Responses.badRequest(message.getMessage("error.evento.modificar"));
    }

    /**
     * Este metodo permite modificar la imagen de un evento
     *
     * @param id    es el id del evento que queremos modificar
     * @param token es el token de autenticacion del usuario
     * @param file  es la imagen que le queremos asignar al evento
     * @return un ResponseEntity indicando que se ha modificado correctamente la imagen o que ha ocurrido algun error
     */
    @PatchMapping("/modificar/imagen/{id}")
    @Operation(summary = "Modificar imagen", description = "Modifica la imagen de un evento")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> modificarImagen(
            @Parameter(description = "Id del animal al que le queremos modificar la imagen") @PathVariable Long id, @RequestHeader("token") String token,
            @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Evento dbEvento = service.buscarPorId(id);
        if (dbEvento == null) {
            return Responses.notFound(message.getMessage("error.evento.id"));
        }
        if (dbEvento.getFoto() != null) {
            File imagen = new File(dbEvento.getFoto());
            imagen.delete();
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }
        dbEvento.setFoto(path);
        if (service.guardar(dbEvento) != null) {
            return ResponseEntity.ok(path);
        }
        return Responses.badRequest(message.getMessage("error.evento.modificar_imagen"));
    }

}
