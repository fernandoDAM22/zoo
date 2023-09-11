package com.proyectozoo.zoo.repository;

import com.proyectozoo.zoo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Ese metodo permite obtener un usuario por su nombre
     * @param nombre es el nombre del usuario que queremos obtener
     * @return un optional que puede contener el usuario o no
     */
    @Query(value = "Select * from usuarios where nombre like :nombre limit 1",nativeQuery = true)
    Optional<Usuario> obtenerPorNombre(@Param("nombre") String nombre);

    /**
     * Este metodo permite obtener un usuario por su email
     * @param email es el email del usuario que queremos obtener
     * @return un optional que puede contener el usuario o no
     */
    @Query(value = "Select * from usuarios where email like :email limit 1", nativeQuery = true)
    Optional<Usuario> obtenerPorEmail(@Param("email") String email);

}
