package com.proyectozoo.zoo.service.impl;

import com.proyectozoo.zoo.entity.Usuario;
import com.proyectozoo.zoo.repository.UsuarioRepository;
import com.proyectozoo.zoo.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Este metodo permite obtener todos los usaurios de la base de datos
     * @return una lista con todos los usuarios de la base de datos
     */
    @Override
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Este metodo permite buscar un usuario por id
     * @param id es el id del usuario a buscar
     * @return el usuario si existe, null si no existe
     */
    @Override
    public Usuario buscarPorId(long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

    /**
     * Este metodo permite buscar un usuario por nombre
     * @param nombre es el nombre del usuario que queremos buscar
     * @return el usuario si existe, null si no existe
     */
    @Override
    public Usuario buscarPorNombre(String nombre){
        Optional<Usuario> optionalUsuario = usuarioRepository.obtenerPorNombre(nombre);
        return optionalUsuario.orElse(null);
    }

    /**
     * Este metodo permite obtener un usuario por su email
     * @param email es el email del usuario que queremos obtener
     * @return el usuario si existe, null si no
     */
    @Override
    public Usuario buscarPorEmail(String email){
        Optional<Usuario> optionalUsuario = usuarioRepository.obtenerPorEmail(email);
        return optionalUsuario.orElse(null);
    }


    /**
     * Este metodo permite guardar un usuario en la base de datos
     * @param usuario es el usuario que se va a guardar
     * @return el usuario guardado
     */
    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Este metodo permite borrar un usuario de la base de datos
     * @param id es el id del usuario a borrar
     * @return el usuario borrado
     */
    @Override
    public Usuario borrar(long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isEmpty()) {
            return null;
        }else{
            Usuario user = usuario.get();
            usuarioRepository.delete(user);
            return user;
        }
    }

    /**
     * Este metodo permite actualizar un usuario
     * @param newUser es el nuevo usuario con los datos a actualizar
     * @return el usuario con los nuevos datos
     */
    @Override
    public Usuario actualizar(Usuario newUser){
        Optional<Usuario> optionalUser = usuarioRepository.findById(newUser.getId());
        if(optionalUser.isEmpty()){
            return null;
        }else{
            Usuario usuario = optionalUser.get();
            usuario.setNombre(newUser.getNombre());
            usuario.setEmail(newUser.getEmail());
            return usuarioRepository.save(usuario);
        }
    }
}
