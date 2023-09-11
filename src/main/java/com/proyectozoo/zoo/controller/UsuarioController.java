package com.proyectozoo.zoo.controller;

import com.proyectozoo.zoo.entity.Usuario;
import com.proyectozoo.zoo.service.IUploadFileService;
import com.proyectozoo.zoo.service.IUsuarioService;
import com.proyectozoo.zoo.util.JWTUtil;
import com.proyectozoo.zoo.util.Responses;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
     * Este metodo permite obtener todos los usuarios de la base de datos
     *
     * @param token es el token de autenticacion de el usuario logueado
     * @return una lista con todos los usuarios de la base de datos
     */
    @GetMapping("/")
    public ResponseEntity<List<Usuario>> obtenerUsuarios(@RequestHeader("token") String token) {
        if (!jwtUtil.validarToken(token) || !jwtUtil.validarAdmin(token)) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticion invalido").body(null);
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
            ResponseEntity.status(HttpStatus.FORBIDDEN).header("Error", "Token de autenticion invalido").body(null);

        }
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Error", "No existe un usuario con ese id").body(null);
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Este metodo permite registrar un usuario
     *
     * @param usuario es el usuario con los datos que vamos a registrar
     * @return un ResponseEntity indicando si se ha registrado correctamente el usuario o no
     */
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        //comprobamos que no exista ya un usuario con ese nombre
        if (usuarioService.buscarPorNombre(usuario.getNombre()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese nombre");
        }
        //Comprobamos que no exista ya un usuario con ese email
        if (usuarioService.buscarPorEmail(usuario.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un usuario con ese email");
        }
        //Ciframos la  contrasena
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
            return ResponseEntity.badRequest().body("Error al registrar el usuario");
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
            return Responses.FORBIDDEN;
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contraseña incorrecta");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credenciales de acceso incorrectas");
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
            return Responses.FORBIDDEN;
        }
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) {
            return Responses.notFound("No existe ningun usuario con ese id");
        }
        File file = new File(usuario.getFoto());
        file.delete();
        if (usuarioService.borrar(id) != null) {
            return ResponseEntity.ok("Usuario borrado correctamente");
        }
        return Responses.badRequest("Error al borrar el usuario");
    }

    /**
     * Este metodo permite modificar un usuario totalmente a excepcion de su foto de la base de datos
     *
     * @param token   es el token de autenticacion del usaurio registrado
     * @param usuario es el usuario con los datos del usuario a modificar
     * @return un ResponseEntity indicando que se ha modificado correctamente el usuario o de que ha ocurrido algun error
     */
    @PutMapping("/")
    public ResponseEntity<String> actualizar(@RequestHeader String token, @RequestBody Usuario usuario) {
        if (!jwtUtil.validarToken(token)) {
            return Responses.FORBIDDEN;
        }
        Long userId = Long.parseLong(jwtUtil.getKey(token));
        Usuario dbUser = usuarioService.buscarPorId(usuario.getId());
        if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
            return Responses.notFound("No existe ningun usuario con ese id");
        }
        if (usuarioService.buscarPorNombre(usuario.getNombre()) != null && !dbUser.getNombre().equals(usuario.getNombre())) {
            return Responses.conflict("Ya existe un usuario con ese nombre");
        }
        if (usuarioService.buscarPorEmail(usuario.getEmail()) != null && !dbUser.getEmail().equals(usuario.getEmail())) {
            return Responses.conflict("Ya existe un usuario con ese nombre");
        }
        if (usuarioService.actualizar(usuario) != null) {
            return ResponseEntity.ok("Usuario modificado correctamente");
        }
        return Responses.badRequest("Error al modificar el usuario");
    }

    /**
     * Este metodo permite actualizar el nombre de un usuario
     *
     * @param token  es el token de autenticacion del usuario logueado
     * @param id     es el id del usuario que queremos modificar
     * @param nombre es el nombre que le vamos a poner al usuario
     * @return un ResponseEntity indicando que se ha modificado correctamente el nombre del usuario o que ha ocurrido algun error
     */
    @PatchMapping("/actualizar/nombre/{id}")
    public ResponseEntity<String> actualizarNombre(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody String nombre) {
        if (!jwtUtil.validarToken(token)) {
            return Responses.FORBIDDEN;
        }
        Long userId = Long.parseLong(jwtUtil.getKey(token));
        Usuario dbUser = usuarioService.buscarPorId(id);
        if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
            return Responses.notFound("No existe ningun usuario con ese id");
        }
        if (usuarioService.buscarPorNombre(nombre) != null && !dbUser.getNombre().equals(nombre)) {
            return Responses.conflict("Ya existe un usuario con ese nombre");
        }
        dbUser.setNombre(nombre);
        if (usuarioService.guardar(dbUser) != null) {
            return ResponseEntity.ok("Nombre actualizado correctamente");
        }
        return Responses.badRequest("Error al modificar el nombre");
    }

    /**
     * Este metodo permite actualizar el email de un usuario
     *
     * @param token es el token de autenticacion del usuario logueado
     * @param id    es el id del usuario que queremos modificar
     * @param email es el email que le vamos a poner al usuario
     * @return un ResponseEntity indicando que se ha modificado correctamente el email del usuario o que ha ocurrido algun error
     */
    @PatchMapping("/actualizar/email/{id}")
    public ResponseEntity<String> actualizarEmail(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody String email) {
        if (!jwtUtil.validarToken(token)) {
            return Responses.FORBIDDEN;
        }
        Long userId = Long.parseLong(jwtUtil.getKey(token));
        Usuario dbUser = usuarioService.buscarPorId(id);
        if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
            return Responses.notFound("No existe ningun usuario con ese id");
        }
        if (usuarioService.buscarPorEmail(email) != null && !dbUser.getEmail().equals(email)) {
            return Responses.conflict("Ya existe un usuario con ese email");
        }
        dbUser.setEmail(email);
        if (usuarioService.guardar(dbUser) != null) {
            return ResponseEntity.ok("Email actualizado correctamente");
        }
        return Responses.badRequest("Error al modificar el email");
    }

    /**
     * Este metodo permite actualizar la contrasena de un usuario
     *
     * @param token    es el token de autenticacion del usuario logueado
     * @param id       es el id del usuario al que le queremos cambiar la contrasena
     * @param password es el nueva contrasena del usuario
     * @return un ResponseEntity indicando que se ha modificaco correctamente la contrasena del usaurio
     */
    @PatchMapping("/actualizar/password/{id}")
    public ResponseEntity<String> actualizarPassword(@RequestHeader("token") String token, @PathVariable Long id, @RequestBody String password) {
        if (!jwtUtil.validarToken(token)) {
            return Responses.FORBIDDEN;
        }
        Long userId = Long.parseLong(jwtUtil.getKey(token));
        Usuario dbUser = usuarioService.buscarPorId(id);
        if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
            return Responses.notFound("No existe ningun usuario con ese id");
        }
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        char[] chain = password.toCharArray();
        String hash = argon2.hash(1, 1024, 1, password);
        dbUser.setPassword(hash);
        if (usuarioService.guardar(dbUser) != null) {
            return ResponseEntity.ok("Contrasena actualizada correctamente");
        }
        return Responses.badRequest("Error al modificar la contrasena");
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
            return Responses.FORBIDDEN;
        }
        Long userId = Long.parseLong(jwtUtil.getKey(token));
        Usuario dbUser = usuarioService.buscarPorId(id);
        if (dbUser == null || !Objects.equals(dbUser.getId(), userId)) {
            return Responses.notFound("No existe ningun usuario con ese id");
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
        return Responses.badRequest("Error al modificar la foto");
    }

}
