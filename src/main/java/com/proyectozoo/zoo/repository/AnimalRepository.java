package com.proyectozoo.zoo.repository;

import com.proyectozoo.zoo.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    /**
     * Esta consulta permite obtener un animal por su nombre
     * @param nombre es el nombre del animal que queremos obtener
     * @return un optional que puede contener el animal o no
     */
    @Query(value = "Select * from animales where nombre like :nombre limit 1",nativeQuery = true)
    Optional<Animal> obtenerPorNombre(@Param("nombre") String nombre);

    /**
     * Este metodo permite obtener todos los animales de una seccion
     * @param id es el id de la seccion de la que queremos obtener los animales
     * @return una lista con todos los animales de esa seccion
     */
    @Query(value = "Select * from animales where id_seccion = :id",nativeQuery = true)
    List<Animal> obtenerAnimalesPorSeccion(@Param("id") Long id);

    /**
     * Este metodo permite obtener el animal con mas comentarios en la ultima semana
     * @return el animal con mas comentarios en la ultima semana
     */
    @Query(value = "SELECT a.* FROM animales a LEFT JOIN comentarios c ON a.id = c.id_animal WHERE c.fecha >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK) GROUP BY a.id, a.nombre ORDER BY COUNT(c.id) DESC LIMIT 1",nativeQuery = true)
    Animal animalMasPopularSemana();

    /**
     * Este metodo permite obtener el animal con mas comentarios en el ultimo mes
     * @return el animal con mas comentarios en el ultimo mes
     */
    @Query(value = "SELECT a.* FROM animales a LEFT JOIN comentarios c ON a.id = c.id_animal WHERE c.fecha >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) GROUP BY a.id, a.nombre ORDER BY COUNT(c.id) DESC LIMIT 1",nativeQuery = true)
    Animal animalMasPopularMes();
}
