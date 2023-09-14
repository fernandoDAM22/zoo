package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.entity.Animal;
import com.proyectozoo.zoo.service.IAnimalService;
import com.proyectozoo.zoo.service.IUploadFileService;
import com.proyectozoo.zoo.util.JWTUtil;
import com.proyectozoo.zoo.util.Responses;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
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
@RequestMapping("api/animales")
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
     * Este metodo permite obtener todos los animales de la base de datos
     *
     * @param token es el token de autenticacion del usuario
     * @return una lista con todos los animales de la base de datos
     */
    @GetMapping("/")
    public ResponseEntity<List<Animal>> obtenerAnimales(@RequestHeader String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
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
    public ResponseEntity<List<Animal>> obtenerAnimalesPorSeccion(@RequestHeader("token") String token, @PathVariable Long id) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
        }
        return ResponseEntity.ok(service.obtenerAnimalesPorSeccion(id));
    }

    /**
     * Este metodo permite dar de alta un animal
     *
     * @param animal        es el animal que queremos dar de alta
     * @param token         es el token de autenticacion del usuario
     * @param bindingResult es el objeto para capturar los errores de validacion
     * @return un ResponseEntity indicando que se ha dado de alta correctamente el animal, o que algo fallo
     */
    @PostMapping("/alta")
    public ResponseEntity<String> insertar(@RequestBody Animal animal, @RequestHeader("token") String token, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
        }
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        if (service.buscarPorNombre(animal.getNombre()) != null) {
            return Responses.conflict("Ya existe un animal con ese nombre");
        }
        animal.setFoto("C://imagenes//zoo//animales//default.png");
        if (service.guardar(animal) != null) {
            return Responses.created(String.valueOf(animal.getId()));
        } else {
            return Responses.notFound("Error al crear el animal");
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
    public ResponseEntity<String> subir(@PathVariable Long id, @RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        Animal animal = service.buscarPorId(id);
        if (animal == null) {
            return Responses.badRequest("No existe un animal con ese id");
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
    public ResponseEntity<String> borrarAnimal(@PathVariable Long id, @RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        Animal animal = service.buscarPorId(id);
        if (animal == null) {
            return Responses.notFound("No existe ningun animal con ese id");
        }
        File file = new File(animal.getFoto());
        file.delete();
        if (service.borrar(id) != null) {
            return ResponseEntity.ok("Animal borrado correctamente");
        } else {
            return Responses.badRequest("Error al borrar el animal");
        }
    }

    /**
     * Este metodo permite moficiar un animal a excepcion de su foto
     *
     * @param token         es el token de autenticacion del usuario que esta intentando modificar el animal
     * @param animal        es el animal con los nuevos datos
     * @param bindingResult es el objeto para capturar los errores de validacion
     * @return un ResponseEntity indicando que se ha modificado correctamente el animal o que ha ocurrido algun error
     */
    @PutMapping("/")
    public ResponseEntity<String> modificarAnimal(@RequestHeader("token") String token, @RequestBody Animal animal, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
                return Responses.FORBIDDEN;
            }
            Animal dbAnimal = service.buscarPorId(animal.getId());
            if (dbAnimal == null) {
                return Responses.notFound("No existe un animal con ese id");
            }
            //en caso de que ya exista un animal en la base de datos con el mismo  nombre y este no sea el animal que
            //estamos intentando modificar
            if (service.buscarPorNombre(animal.getNombre()) != null && !dbAnimal.getNombre().equals(animal.getNombre())) {
                return Responses.conflict("Ya existe un animal con ese nombre");
            }
            if (service.actualizar(animal) != null) {
                return ResponseEntity.ok("Animal modificiado correctamente");
            }
            return Responses.badRequest("No se ha podido mofificar el animal");
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
    public ResponseEntity<String> modificarImagen(@PathVariable Long id, @RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.FORBIDDEN;
        }
        Animal dbAnimal = service.buscarPorId(id);
        if (dbAnimal == null) {
            return Responses.notFound("No existe un animal con ese id");
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
        return Responses.badRequest("Error al modificar la imagen");
    }

    /**
     * Este metodo permite obtener el animal mas popular de la semana
     *
     * @param token es el token de autenticacion del usuario
     * @return el animal con mas comentarios en la ultima semana
     */
    @GetMapping("/popular/semana")
    public ResponseEntity<Animal> animalMasPopularSemana(@RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
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
    public ResponseEntity<Animal> animalMasPopularMes(@RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticacion invalido").body(null);
        }
        return ResponseEntity.ok(service.animalMasVotadoMes());
    }
}
