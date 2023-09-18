package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.components.ErrorUtils;
import com.proyectozoo.zoo.components.JWTUtil;
import com.proyectozoo.zoo.components.MessageComponent;
import com.proyectozoo.zoo.entity.Usuario;
import com.proyectozoo.zoo.service.IUploadFileService;
import com.proyectozoo.zoo.service.IUsuarioService;
import com.proyectozoo.zoo.util.Responses;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping("api/usuarios")
public class UsuarioController {
    /**
     * Instancia del servicio
     */
    @Autowired
    private IUsuarioService usuarioService;
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
    @Value("${ruta.imagenes.usuarios}")
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
     * Este metodo permite obtener todos los usuarios de la base de datos
     *
     * @param token es el token de autenticacion de el usuario logueado
     * @return una lista con todos los usuarios de la base de datos
     */
    @GetMapping("/")
    public ResponseEntity<List<Usuario>> obtenerUsuarios(@RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);
        }
        return ResponseEntity.ok(usuarioService.obtenerUsuarios());
    }

    /**
     * Este metodo permite obtener un usuario de la base de datos
     *
     * @param token es el token de autenticacion del usaurio logueado
     * @param id    es el id del usuario que queremos obtener
     * @return un ResponseEntity con el usuario en caso de que exista, o un error en caso de que ocurra
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@RequestHeader String token, @PathVariable Long id) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", message.getMessage("error.usuario.token")).body(null);

        }
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error", message.getMessage("error.usuario.id")).body(null);
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Este metodo permite registrar un usuario
     *
     * @param usuario       es el usuario con los datos que vamos a registrar
     * @param bindingResult es el objeto que nos permite capturar los errores de validacion
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando si se ha registrado correctamente el usuario o no
     */
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            char[] password = usuario.getPassword().toCharArray();
            String hash = argon2.hash(1, 1024, 1, password);
            //se la asignamos al usuario
            usuario.setPassword(hash);
            usuario.setFoto("C://imagenes//zoo//usuarios//default.png");
            //asignamos el tipo user ya que todos los usuarios que se registren a traves de la pagina seran usuarios normales
            usuario.setTipo("USER");
            //si se guarda retornamos su id, en caso contrario retornamos un mensaje de error
            if (usuarioService.guardar(usuario) != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(usuario.getId()));
            } else {
                return ResponseEntity.badRequest().body(message.getMessage("error.usuario.registrar"));
            }
        } catch (DataIntegrityViolationException ex) {
            return Responses.badRequest(message.getMessage("error.usuario.datos"));
        }
    }

    /**
     * Este metodo permite subir para un usuario
     *
     * @param id   es el id del usuario que queremos subir
     * @param file es la foto que queremos subir
     * @return un mensaje indicando que se ha subido la imagen correctamente o no
     */
    @PostMapping("/imagen/{id}")
    public ResponseEntity<String> subir(@PathVariable Long id, @RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario != null) {
            usuario.setFoto(path);
            usuarioService.guardar(usuario);
        }
        return ResponseEntity.ok(path);
    }

    /**
     * Este metodo permite que un usuario inicie sesion en el sistema
     *
     * @param usuario es el usuario que contiene las credenciales
     * @return un ResponseEntity indicando que se ha logueado correctamente el usuario, o que ha fallado el proceso
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        Usuario authUser = usuarioService.buscarPorEmail(usuario.getEmail());
        System.out.println(authUser);
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (authUser != null) {
            if (argon2.verify(authUser.getPassword(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.OK).body(jwtUtil.create(String.valueOf(authUser.getId()), authUser.getEmail()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message.getMessage("error.usuario.password"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message.getMessage("error.usuario.credenciales"));
        }
    }

    /**
     * Este metodo permite borrar un usuario de la base de datos
     *
     * @param token es el token de autenticacion del usuario que queremos borrar
     * @param id    es el id del usuario que queremos borrar
     * @return un ReponseEntity indicando que se ha borrado correctamente el usuario o que ha ocurrido algun error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> borrar(@RequestHeader("token") String token, @PathVariable Long id) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return Responses.notFound(message.getMessage("error.usuario.id"));
        }
        File file = new File(usuario.getFoto());
        file.delete();
        if (usuarioService.borrar(id) != null) {
            return ResponseEntity.ok(message.getMessage("mensaje.usuario.borrado"));
        }
        return Responses.badRequest(message.getMessage("error.usuario.borrar"));
    }

    /**
     * Este metodo permite modificar un usuario totalmente a excepcion de su foto de la base de datos
     *
     * @param token         es el token de autenticacion del usaurio registrado
     * @param usuario       es el usuario con los datos del usuario a modificar
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha modificado correctamente el usuario o de que ha ocurrido algun error
     */
    @PutMapping("/")
    public ResponseEntity<String> actualizar(@RequestHeader String token, @Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token)) {
                return Responses.forbidden(message.getMessage("error.usuario.token"));
            }
            Long userId = Long.parseLong(jwtUtil.getKey(token));
            Usuario dbUser = usuarioService.buscarPorId(usuario.getId());
            if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
                return Responses.notFound(message.getMessage("error,usuario.id"));
            }
            if (usuarioService.actualizar(usuario) != null) {
                return ResponseEntity.ok(message.getMessage("mensaje.usuario.actualizado"));
            }
            return Responses.conflict(message.getMessage("error.usuario.modificar"));
        } catch (DataIntegrityViolationException ex) {
            return Responses.conflict(message.getMessage("error.usuario.datos"));
        }
    }

    /**
     * Este metodo permite actualizar el nombre de un usuario
     *
     * @param token         es el token de autenticacion del usuario logueado
     * @param id            es el id del usuario que queremos modificar
     * @param nombre        es el nombre que le vamos a poner al usuario
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha modificado correctamente el nombre del usuario o que ha ocurrido algun error
     */
    @PatchMapping("/actualizar/nombre/{id}")
    public ResponseEntity<String> actualizarNombre(@RequestHeader("token") String token, @PathVariable Long id, @Valid @RequestBody String nombre, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token)) {
                return Responses.forbidden(message.getMessage("error.usuario.token"));
            }
            Long userId = Long.parseLong(jwtUtil.getKey(token));
            Usuario dbUser = usuarioService.buscarPorId(id);
            if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
                return Responses.notFound(message.getMessage("error.usuario.id"));
            }
            dbUser.setNombre(nombre);
            if (usuarioService.guardar(dbUser) != null) {
                return ResponseEntity.ok(message.getMessage("mensaje.usuario.nombre_actualiazado"));
            }
            return Responses.badRequest(message.getMessage("error.usuario.modificar_nombre"));
        } catch (DataIntegrityViolationException ex) {
            return Responses.conflict(message.getMessage("error.usuario.existe_nombre"));
        }
    }

    /**
     * Este metodo permite actualizar el email de un usuario
     *
     * @param token         es el token de autenticacion del usuario logueado
     * @param id            es el id del usuario que queremos modificar
     * @param email         es el email que le vamos a poner al usuario
     * @param bindingResult objeto para poder realizar la validacion de los campos
     * @return un ResponseEntity indicando que se ha modificado correctamente el email del usuario o que ha ocurrido algun error
     */
    @PatchMapping("/actualizar/email/{id}")
    public ResponseEntity<?> actualizarEmail(@RequestHeader("token") String token, @PathVariable Long id, @Valid @RequestBody String email, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUtils.getErrorMessages(bindingResult).toString());
            }
            if (!jwtUtil.validarToken(token)) {
                return Responses.forbidden(message.getMessage("error.usuario.token"));
            }
            Long userId = Long.parseLong(jwtUtil.getKey(token));
            Usuario dbUser = usuarioService.buscarPorId(id);
            if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
                return Responses.notFound(message.getMessage("error.usuario.id"));
            }
            dbUser.setEmail(email);
            if (usuarioService.guardar(dbUser) != null) {
                return ResponseEntity.ok(message.getMessage("mensaje.usuario.email_actualizado"));
            }
            return Responses.badRequest(message.getMessage("error.usuario.modificar_email"));
        } catch (DataIntegrityViolationException ex) {
            return Responses.conflict(message.getMessage("error.usuario.existe_email"));
        }
    }

    /**
     * Este metodo permite actualizar la contrasena de un usuario
     *
     * @param token         es el token de autenticacion del usuario logueado
     * @param id            es el id del usuario al que le queremos cambiar la contrasena
     * @param password      es el nueva contrasena del usuario
     * @return un ResponseEntity indicando que se ha modificaco correctamente la contrasena del usaurio
     */
    @PatchMapping("/actualizar/password/{id}")
    public ResponseEntity<String> actualizarPassword(@RequestHeader("token") String token, @PathVariable Long id, @Valid @RequestBody String password) {
            if (!jwtUtil.validarToken(token)) {
                return Responses.forbidden(message.getMessage("error.usuario.token"));
            }
            Long userId = Long.parseLong(jwtUtil.getKey(token));
            Usuario dbUser = usuarioService.buscarPorId(id);
            if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
                return Responses.notFound(message.getMessage("error.usuario.id"));
            }
            /*
             * Como la contraseña se cifra, aunque se introduzca una contrasena con menos de 8
             * caracteres al aplicar el proceso de cifrado esta superara los 8 caracteres, por lo que
             * la validacion automatica de Spring no funcionara y tenemos que realizarla nosotros manualmente
             */
            if (password.length() < 8) {
                return ResponseEntity.badRequest().body(message.getMessage("error.usuario.longitud_contrasena"));
            }
            Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
            String hash = argon2.hash(1, 1024, 1, password);
            dbUser.setPassword(hash);
            if (usuarioService.guardar(dbUser) != null) {
                return ResponseEntity.ok(message.getMessage("mensaje.usuario.contrasena_actualizada"));
            }
        return Responses.badRequest(message.getMessage("error.usuario.modificar_contrasena"));
    }

    /**
     * Este metodo permite modificar la foto de un usuario
     *
     * @param token es el token del usuario que quiere modificar su foto
     * @param id    es el id del usuario al que le vamos a modificar la foto
     * @param file  es la nueva foto del usuario
     * @return un ResponseEntity de String indicado que se ha modificado la foto o de que ha ocurrido algun error
     */
    @PatchMapping("/actualizar/imagen/{id}")
    public ResponseEntity<String> actualizarFoto(@RequestHeader("token") String token, @PathVariable Long id, MultipartFile file) {
        if (!jwtUtil.validarToken(token)) {
            return Responses.forbidden(message.getMessage("error.usuario.token"));
        }
        Long userId = Long.parseLong(jwtUtil.getKey(token));
        Usuario dbUser = usuarioService.buscarPorId(id);
        if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
            return Responses.notFound(message.getMessage("error.usuario.id"));
        }
        if (dbUser.getFoto() != null) {
            File imagen = new File(dbUser.getFoto());
            imagen.delete();
        }
        String path = uploadFileService.subirImagen(file, carpetaImagenes);
        if (!path.startsWith("C:")) {
            return Responses.badRequest(path);
        }
        dbUser.setFoto(path);
        if (usuarioService.guardar(dbUser) != null) {
            return ResponseEntity.ok(path);
        }
        return Responses.badRequest(message.getMessage("error.usuario.modificar_foto"));
    }

}
