package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.components.JWTUtil;
import com.proyectozoo.zoo.components.MessageComponent;
import com.proyectozoo.zoo.entity.Seccion;
import com.proyectozoo.zoo.service.ISeccionService;
import com.proyectozoo.zoo.service.IUploadFileService;
import com.proyectozoo.zoo.util.Responses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("api/secciones")
@Tag(name = "Secciones", description = "Operaciones relacionadas con las secciones")
public class SeccionController {
    /**
     * Instancia del servicio
     */
    @Autowired
    private ISeccionService service;
    /**
     * Instancia de JWTUtil para poder acceder a la logica de JWT
     */
    @Autowired
    private JWTUtil jwtUtil;
    /**
     * Instancia del servicio que nos permite subir imagenes al servidor
     */
    @Autowired
    private IUploadFileService uploadFileService;
    /**
     * Ruta a la carpeta donde se suben las imagenes
     */
    @Value("${ruta.imagenes.secciones}")
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
     * Este metodo permite dar de alta una seccion
     *
     * @param seccion       es la seccion que vamos a dar de alta
     * @param token         es el token de autenticacion del administrador
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un responseEntity indicando que se ha dado de alta la seccion o que ha ocurrido algun error
     */
    @PostMapping("/alta")
    @Operation(summary = "Inserta una seccion", description = "Inserta una seccion en la base de datos")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> alta(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader(name = "token") String token,
            @Valid @RequestBody Seccion seccion, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
                return Responses.forbidden(message.getMessage("error.usuario.token"));
            }
            seccion.setFoto("C://imagenes//zoo//secciones//default.png");
            if (service.guardar(seccion) != null) {
                return Responses.created(String.valueOf(seccion.getId()));
            } else {
                return Responses.notFound(message.getMessage("error.seccion.crear"));
            }
        } catch (DataIntegrityViolationException ex) {
            return Responses.conflict(message.getMessage("error.seccion.nombre_repetido"));
        }
    }

    /**
     * Este metodo permite asignarle una imagen a una seccion
     *
     * @param id   es el id de la seccion a la que le vamos a asignar la imagen
     * @param file es la imagen que vamos a subir
     * @return un responseEntity indicando que se ha subido correctamente la imagen o
     * que ha ocurrido algun error
     */
    @PostMapping("/imagen/{id}")
    @Operation(summary = "Subir una imagen", description = "Sube una imagen al servidor y se la asigna a una seccion")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> subirImagen(
            @Parameter(description = "id de la seccion a la que le queremos asignar la imagen") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Parameter(description = "Imagen que le queremos asignar a una seccion") @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }
        Seccion seccion = service.buscarPorId(id);
        if (seccion != null) {
            seccion.setFoto(path);
            service.guardar(seccion);
        }
        return ResponseEntity.ok(path);
    }

    /**
     * Este metodo pemite obtener los nombres de las secciones
     *
     * @return una lista con los nombres de las secciones
     */
    @GetMapping("/nombres")
    @Operation(summary = "Obtener nombres", description = "Obtiene una lista con todos los nombres de las secciones de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> obtenerNombres() {
        return ResponseEntity.ok(service.obtenerNombres());
    }

    /**
     * Este metodo permite obtener todas las secciones de la base de datos
     *
     * @return una lista con todas las secciones de la base de datos
     */
    @GetMapping("/")
    @Operation(summary = "Obtener secciones", description = "Obtiene una lista con todas las secciones de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Seccion>> obtenerSecciones() {
        return ResponseEntity.ok(service.obtenerSecciones());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> borrarSeccion(
            @Parameter(description = "Id de la seccion que queremos borrar") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Seccion seccion = service.buscarPorId(id);
        File file = new File(seccion.getFoto());
        file.delete();
        if (service.borrar(id) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.seccion.borrada"));
        } else {
            return Responses.badRequest(message.getMessage("error.seccion.borrar"));
        }
    }

    /**
     * Este metodo permite modificar una seccion a excepcion de su foto
     *
     * @param token         es el token de autenticacion del usuario que esta intentando modificar la secccion
     * @param seccion       es la seccion con los datos a modificar
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha modificado correctamente la seccion o que ha ocurrido algun error
     */
    @PutMapping("/modificar")
    @Operation(summary = "Modificar seccion", description = "Modifica una seccion a excepcion de su foto")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> modificar(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader String token,
            @Valid @RequestBody Seccion seccion, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Seccion dbSeccion = service.buscarPorId(seccion.getId());
        if (dbSeccion == null) {
            return Responses.notFound(message.getMessage("error.seccion.id"));
        }
        if (service.buscarPorNombre(seccion.getNombre()) != null && !dbSeccion.getNombre().equals(seccion.getNombre())) {
            return Responses.conflict(message.getMessage("error.seccion.nombre_repetido"));
        }
        if (service.actualizar(seccion) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.seccion.actualizada"));
        }
        return Responses.badRequest(message.getMessage("error.seccion.actualizar"));
    }

    /**
     * Este metodo permite modificar la imagen de una seccion
     *
     * @param id    es el id de la seccion que queremos modificar
     * @param token es el token de autenticacion del usuario que esta intentando modificar la imagen
     * @param file  es la nueva imagen que se le va a asignar a la seccion
     * @return un ResponseEntity indicando que se ha modificado correctamente la seccion o que ha ocurrido algun error
     */
    @PatchMapping("/modificar/imagen/{id}")
    @Operation(summary = "Modificar imagen", description = "Modifica la imagen de una seccion")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> modificarImagen(
            @Parameter(description = "Id de la seccion a la que le queremos modificar la imagen") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader String token,
            @Parameter(description = "Imagen que le queremos asignar a la seccion") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Seccion dbSeccion = service.buscarPorId(id);
        if (dbSeccion == null) {
            return Responses.notFound(message.getMessage("error.seccion.id"));
        }
        if (dbSeccion.getFoto() != null) {
            File imagen = new File(dbSeccion.getFoto());
            imagen.delete();
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }
        dbSeccion.setFoto(path);
        if (service.guardar(dbSeccion) != null) {
            return ResponseEntity.ok(path);
        }
        return Responses.badRequest(message.getMessage("error.seccion.modificar_imagen"));
    }
}
