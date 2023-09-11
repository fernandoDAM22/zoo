package com.proyectozoo.zoo.repository;

import com.proyectozoo.zoo.entity.Seccion;
import org.aspectj.weaver.ast.Var;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {
    /**
     * Este metodo permite obtener una seccion por su nombre
     * @param nombre es el nombre de la seccion que queremos obtener
     * @return un optional que puede contener la seccion o no
     */
    @Query(value = "Select * from secciones where nombre like :nombre", nativeQuery = true)
    Optional<Seccion> obtenerPorNombre(@Param("nombre") String nombre);

    /**
     * Este metodo permite obtener el nombre de todas las secciones
     * @return una lista con todos los nombres de las secciones
     */
    @Query(value = "Select nombre from secciones",nativeQuery = true)
    List<String> obtenerNombres();
}
