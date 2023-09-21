package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.components.JWTUtil;
import com.proyectozoo.zoo.components.MessageComponent;
import com.proyectozoo.zoo.entity.Animal;
import com.proyectozoo.zoo.service.IAnimalService;
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
@RequestMapping("api/animales")
@Tag(name = "Animales", description = "Operaciones relacionadas con los animales")
public class AnimalController {
    /**
     * Instancia del servicio
     */
    @Autowired
    private IAnimalService service;
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
    @Value("${ruta.imagenes.animales}")
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
     * Este metodo permite obtener todos los animales de la base de datos
     *
     * @param token es el token de autenticacion del usuario
     * @return una lista con todos los animales de la base de datos
     */
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener la lista de animales", description = "Obtiene una lista con todos los animales de la base de datos")
    public ResponseEntity<List<Animal>> obtenerAnimales(@Parameter(description = "token de autenticacion del usuario") @RequestHeader String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token_expirado")).body(null);
        }
        return ResponseEntity.ok(service.obtenerAnimales());
    }

    /**
     * Este metodo permite obtener todos los animales de una seccion
     *
     * @param token es el token de autenticacion del usuario
     * @param id    es el id de la seccion de la cual queremos obtener los animales
     * @return una lista con los animales de esa seccion
     */
    @GetMapping("/seccion/{id}")
    @Operation(summary = "Obtener la lista de animales de una seccion", description = "Obtiene una lista con todos los animales de la seccion indicada")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Animal>> obtenerAnimalesPorSeccion(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Parameter(description = "Id de la categoria de la que queremos obtener los animales") @PathVariable Long id) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(service.obtenerAnimalesPorSeccion(id));
    }

    /**
     * Este metodo permite dar de alta un animal
     *
     * @param animal        es el animal que queremos dar de alta
     * @param token         es el token de autenticacion del usuario
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha dado de alta correctamente el animal, o que algo fallo
     */
    @PostMapping("/alta")
    @Operation(summary = "Insertar un animal", description = "Inserta un animal en la base de datos")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> insertar(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Valid @RequestBody() Animal animal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        if (service.buscarPorNombre(animal.getNombre()) != null) {
            return Responses.conflict(message.getMessage("error.animal.nombre_repetido"));
        }
        animal.setFoto("C://imagenes//zoo//animales//default.png");
        if (service.guardar(animal) != null) {
            return Responses.created(String.valueOf(animal.getId()));
        } else {
            return Responses.notFound(message.getMessage("error.animal.insertar"));
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
    @Operation(summary = "Asignar imagen a un animal", description = "Sube una imagen al servidor y se la asigna a un animal")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> subir(
            @Parameter(description = "Id del animal al que le queremos asignar la imagen") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Parameter(description = "Imagen que le queremos asignar al usuario") @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Animal animal = service.buscarPorId(id);
        if (animal == null) {
            return Responses.badRequest(message.getMessage("error.animal.id"));
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }

        animal.setFoto(path);
        service.guardar(animal);

        return ResponseEntity.ok(path);
    }

    /**
     * Este metodo permite borrar un animal
     *
     * @param id    es el id del animal que queremos borrar
     * @param token es el token de autenticacion del usuario
     * @return un ResponseEntity indicando que se ha borrado correctamente el animal, o que ha ocurrido algun error
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Borra un animal", description = "Borra un animal de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> borrarAnimal(
            @Parameter(description = "Id del animal que queremos borrar") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Animal animal = service.buscarPorId(id);
        if (animal == null) {
            return Responses.notFound(message.getMessage("error.animal.id"));
        }
        File file = new File(animal.getFoto());
        file.delete();
        if (service.borrar(id) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.animal.borrado"));
        } else {
            return Responses.badRequest(message.getMessage("error.animal.borrar"));
        }
    }

    /**
     * Este metodo permite moficiar un animal a excepcion de su foto
     *
     * @param token         es el token de autenticacion del usuario que esta intentando modificar el animal
     * @param animal        es el animal con los nuevos datos
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha modificado correctamente el animal o que ha ocurrido algun error
     */
    @PutMapping("/")
    @Operation(summary = "Borra un animal", description = "Modifica un animal de la base de datos a excepcion de su foto")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> modificarAnimal(
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @Valid @RequestBody Animal animal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Animal dbAnimal = service.buscarPorId(animal.getId());
        if (dbAnimal == null) {
            return Responses.notFound(message.getMessage("error.animal.id"));
        }
        //en caso de que ya exista un animal en la base de datos con el mismo  nombre y este no sea el animal que
        //estamos intentando modificar
        if (service.buscarPorNombre(animal.getNombre()) != null && !dbAnimal.getNombre().equals(animal.getNombre())) {
            return Responses.conflict(message.getMessage("error.animal.nombre_repetido"));
        }
        if (service.actualizar(animal) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.animal.modificado"));
        }
        return Responses.badRequest(message.getMessage("error.animal.modificar"));
    }


    /**
     * Este metodo permite modificar la imagen de un animal
     *
     * @param id    es el id del animal al que le queremos modificar la imagen
     * @param token es el token de autenticacion del usuario que esta intentando modificar la imagen del animal
     * @param file  es la nueva imagen que le vamos a poner al animal
     * @return la ruta de la imagen en el servidor
     */
    @PatchMapping("/modificar/imagen/{id}")
    @Operation(summary = "Modifica la foto de un animal", description = "Modifica la foto de un animal de la base de datos")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> modificarImagen(
            @Parameter(description = "id del animal al que le queremos modificar la imagen") @PathVariable Long id,
            @Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token,
            @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Animal dbAnimal = service.buscarPorId(id);
        if (dbAnimal == null) {
            return Responses.notFound(message.getMessage("error.animal.id"));
        }
        if (dbAnimal.getFoto() != null) {
            File imagen = new File(dbAnimal.getFoto());
            imagen.delete();
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }
        dbAnimal.setFoto(path);
        if (service.guardar(dbAnimal) != null) {
            return ResponseEntity.ok(path);
        }
        return Responses.badRequest(message.getMessage("error.animal.modificar_imagen"));
    }

    /**
     * Este metodo permite obtener el animal mas popular de la semana
     *
     * @param token es el token de autenticacion del usuario
     * @return el animal con mas comentarios en la ultima semana
     */
    @GetMapping("/popular/semana")
    @Operation(summary = "Obtiene el animal mas popupar de la semana", description = "Obtiene en animal con mas comentarios en la ultima semana")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Animal> animalMasPopularSemana(@Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(service.animalMasVotadoSemana());
    }

    /**
     * Este metodo permite obtener el animal mas popular del mes
     *
     * @param token es el token de autenticacion del usuario
     * @return el animal con mas comentarios en el ultimo mes
     */
    @GetMapping("/popular/mes")
    @Operation(summary = "Obtiene el animal mas popupar del mes", description = "Obtiene en animal con mas comentarios en el ultimo mes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Animal> animalMasPopularMes(@Parameter(description = "token de autenticacion del usuario") @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(service.animalMasVotadoMes());
    }
}
