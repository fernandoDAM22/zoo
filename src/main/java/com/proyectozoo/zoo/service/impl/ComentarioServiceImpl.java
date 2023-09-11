package com.proyectozoo.zoo.service.impl;

import com.proyectozoo.zoo.entity.Comentario;
import com.proyectozoo.zoo.repository.ComentarioRepository;
import com.proyectozoo.zoo.service.IComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComentarioServiceImpl implements IComentarioService {
    /**
     * Intancia del repositorio para poder acceder a la base de datos
     */
    @Autowired
    private ComentarioRepository comentarioRepository;

    /**
     * Este metodo permite obtener todos los comentarios de la base de datos
     *
     * @return una lista con todos los comentarios de la base de datos
     */
    @Override
    public List<Comentario> obtener() {
        return comentarioRepository.findAll();
    }

    /**
     * Este metodo permite obtener un comenario por su id
     *
     * @param id es el id del comentario que queremos obtener
     * @return el comentario con el id indicado si existe, null si no existe
     */
    @Override
    public Comentario obtenerPorId(Long id) {
        Optional<Comentario> comentario = comentarioRepository.findById(id);
        return comentario.orElse(null);
    }

    /**
     * Este metodo permite obtener una lista con todos los comentarios de una animal
     * @param id es el id del animal del que queremos obtener sus comentarios
     * @return una lista con todos los comentarios de ese animal
     */
    @Override
    public List<Comentario> obtenerComentariosPorAnimal(Long id) {
        return comentarioRepository.obtenerComentariosPorAnimal(id);
    }

    /**
     * Este metodo permite guardar un comentario en la base de datos
     * @param comentario es el comentario que queremos guardar
     * @return el comentario guardado en la base de datos
     */
    @Override
    public Comentario guardar(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    /**
     * Este metodo permite borrar un comentario
     * @param id es el id del comentario que queremos borrar
     * @return el comentario borrado
     */
    @Override
    public Comentario borrar(Long id) {
        Optional<Comentario> comentario = comentarioRepository.findById(id);
        if (comentario.isEmpty()) {
            return null;
        }
        comentarioRepository.delete(comentario.get());
        return comentario.get();
    }

    /**
     * Este metodo permite comprobar si tenemos un comentario disponible para un animal
     * @param idUsuario es el id del usuario
     * @param idAnimal es el id del animal
     * @return true si tenemos un comentario disponible para ese animal, false si no
     */
    @Override
    public boolean comentarioDisponible(Long idUsuario, Long idAnimal) {
        Optional<Comentario> comentario = comentarioRepository.comentarioDisponible(idUsuario, idAnimal);
        return comentario.isEmpty();
    }
}
