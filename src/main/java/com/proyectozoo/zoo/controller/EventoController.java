package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.entity.Evento;
import com.proyectozoo.zoo.service.IEventoService;
import com.proyectozoo.zoo.service.IUploadFileService;
import com.proyectozoo.zoo.util.JWTUtil;
import com.proyectozoo.zoo.util.Responses;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


@Controller
@RequestMapping("api/eventos")
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
     * Este metodo permite obtener todos los eventos de la base de datos
     *
     * @param token es el token de autenticacion del usuario que esta intentanco obtener los eventos
     * @return una lista con todos los eventos, o un error
     */
    @GetMapping("/")
    public ResponseEntity<List<Evento>> obtener(@RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
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
    public ResponseEntity<List<Evento>> obtenerEventosPorSeccion(@PathVariable Long id, @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
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
    public ResponseEntity<Evento> obtenerEvento(@PathVariable Long id, @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
        }
        Evento evento = service.buscarPorId(id);
        if (evento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error", "No existe un evento con ese id").body(null);
        }
        return ResponseEntity.ok(evento);
    }

    /**
     * Este metodo permite insertar un evento
     *
     * @param token  es el token de autenticacion del usuario
     * @param evento es el evento que queremos insertar
     * @param bindingResult es el objeto para poder capturar los errores de validacion
     * @return un ResponseEntity indicando que se ha insertado correctamente el evento o que ha ocurrido algun error
     */
    @PostMapping("/")
    public ResponseEntity<String> insertar(@RequestHeader("token") String token, @RequestBody Evento evento, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
                return Responses.FORBIDDEN;
            }
            if (service.buscarPorNombre(evento.getNombre()) != null) {
                return Responses.badRequest("Ya existe un evento con ese nombre");
            }
            evento.setFoto("C://imagenes//zoo//eventos//default.png");
            if (service.guardar(evento) != null) {
                return ResponseEntity.ok("Evento guardado correctamente");
            }
            return Responses.badRequest("Error al guardar el evento");
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
    public ResponseEntity<String> subir(@PathVariable Long id, @RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        Evento evento = service.buscarPorId(id);
        if (evento == null) {
            return Responses.badRequest("No existe un evento con ese nombre");
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
    public ResponseEntity<String> borrar(@RequestHeader("token") String token, @PathVariable Long id) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        Evento dbEvento = service.buscarPorId(id);
        if (dbEvento == null) {
            return Responses.badRequest("No existe un evento con ese id");
        }
        File file = new File(dbEvento.getFoto());
        file.delete();
        if (service.borrar(id) != null) {
            return ResponseEntity.ok("Evento borrado correctamente");
        }
        return Responses.badRequest("Error al borrar el evento");
    }

    /**
     * Este metodo permite modificar un evento
     *
     * @param token  es el token de autenticacion del usuario que esta intentando modificar el evento
     * @param evento es el evento que queremos modificar
     * @param bindingResult es el objeto para poder capturar los errores de validacion
     * @return un ResponseEntity indicando que se ha modificado el evento correctamente o que ha ocurrido algun error
     */
    @PutMapping("/")
    public ResponseEntity<String> modificarEvento(@RequestHeader("token") String token, @RequestBody Evento evento, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
                return Responses.FORBIDDEN;
            }
            Evento dbEvento = service.buscarPorId(evento.getId());
            if (dbEvento == null) {
                return Responses.notFound("No existe ningun evento con ese id");
            }
            if (service.buscarPorNombre(evento.getNombre()) != null && !evento.getNombre().equals(dbEvento.getNombre())) {
                return Responses.conflict("Ya existe un evento con ese nombre");
            }
            if (service.modificar(evento) != null) {
                return ResponseEntity.ok("Evento modificado correctamente");
            }
            return Responses.badRequest("Error al modificar el evento");
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
    public ResponseEntity<String> modificarImagen(@PathVariable Long id, @RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        Evento dbEvento = service.buscarPorId(id);
        if (dbEvento == null) {
            return Responses.notFound("No existe ningun evento con ese id");
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
        return Responses.badRequest("No se ha podido modificar la imagen");
    }

}
