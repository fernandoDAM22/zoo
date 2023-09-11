package com.proyectozoo.zoo.service;

import com.proyectozoo.zoo.entity.Comentario;

import java.util.List;
import java.util.Optional;

public interface IComentarioService {
    List<Comentario> obtener();
    Comentario obtenerPorId(Long id);
    List<Comentario> obtenerComentariosPorAnimal(Long id);
    Comentario guardar(Comentario comentario);
    Comentario borrar(Long id);
    boolean comentarioDisponible(Long idUsuario, Long idAnimal);
}
