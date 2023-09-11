package com.proyectozoo.zoo.service;

import com.proyectozoo.zoo.entity.Usuario;

import java.util.List;

public interface IUsuarioService {
    List<Usuario> obtenerUsuarios();
    public Usuario buscarPorId(long id);
    public Usuario buscarPorNombre(String nombre);
    public Usuario buscarPorEmail(String email);
    public Usuario guardar(Usuario usuario);
    public Usuario borrar(long id);
    public Usuario actualizar(Usuario newUser);

}
