package com.proyectozoo.zoo.repository;

import com.proyectozoo.zoo.entity.Evento;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento,Long> {
    /**
     * Este metodo permite obtener un evento por su nombre
     * @param nombre es el nombre del evento que queremos obtener
     * @return un optional que puede contener el evento o no
     */
    @Query(value = "Select * from eventos where nombre like :nombre limit 1",nativeQuery = true)
    Optional<Evento> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Este metodo permite obtener todos los eventos de una seccion
     * @param id es el id de la seccion de la que queremos obtener los eventos
     * @return una lista con todos los eventos de esa seccion
     */
    @Query(value = "Select * from eventos where id_seccion like :id",nativeQuery = true)
    List<Evento> obtenerEventosPorSeccion(@Param("id") Long id);
}
