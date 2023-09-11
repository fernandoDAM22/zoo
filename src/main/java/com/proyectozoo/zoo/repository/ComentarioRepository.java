package com.proyectozoo.zoo.repository;

import com.proyectozoo.zoo.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario,Long> {
    /**
     * Este metodo permite obtener todos los comentarios de un animal
     * @param id es el id del animal del que queremos obtener los comentarios
     * @return una lista con todos los comentarios del animal
     */
    @Query(value = "Select * from comentarios where id_animal = :id",nativeQuery = true)
    List<Comentario> obtenerComentariosPorAnimal(@Param("id") Long id);

    /**
     * Este metodo permite obtener el comentario de un usuario a un animal en el dia de hoy
     * @param idUsuario es el id del usuario que dejo el comentario
     * @param idAnimal es el id del animal al que le dejo el comentario
     * @return un optional que puede contener el comentario o no
     */
    @Query(value = "Select * from comentarios where id_usuario = :idUsuario and id_animal = :idAnimal and DATE(fecha) = CURRENT_DATE",nativeQuery = true)
    Optional<Comentario> comentarioDisponible(@Param("idUsuario") Long idUsuario, @Param("idAnimal") Long idAnimal);
}
