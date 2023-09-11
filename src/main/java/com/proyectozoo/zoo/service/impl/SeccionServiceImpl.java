package com.proyectozoo.zoo.service.impl;

import com.proyectozoo.zoo.entity.Seccion;
import com.proyectozoo.zoo.repository.SeccionRepository;
import com.proyectozoo.zoo.service.ISeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeccionServiceImpl implements ISeccionService {
    @Autowired
    private SeccionRepository seccionRepository;

    /**
     * Este metodo permite obtener todas las secciones
     * @return una lista con todas las secciones de la base de datos
     */
    @Override
    public List<Seccion> obtenerSecciones() {
        return seccionRepository.findAll();
    }

    /**
     * Este metodo permite buscar una seccion por su id
     * @param id es el id de la seccion que queremos obtener
     * @return la seccion con ese id si existe, null si no existe
     */
    @Override
    public Seccion buscarPorId(long id) {
        Optional<Seccion> seccion = seccionRepository.findById(id);
        return seccion.orElse(null);
    }

    /**
     * Este metodo permite buscar una seccion por su nombre
     * @param nombre es el nombre de la seccion que queremos obtener
     * @return la seccion con ese nombre si existe, null si no
     */
    @Override
    public Seccion buscarPorNombre(String nombre) {
        Optional<Seccion> seccion = seccionRepository.obtenerPorNombre(nombre);
        return seccion.orElse(null);
    }

    /**
     * Este metodo permite guardar una seccion
     * @param seccion es la seccion que vamos a guardar
     * @return la seccion guardada, null si ocurre algun error
     */
    @Override
    public Seccion guardar(Seccion seccion) {
        return seccionRepository.save(seccion);
    }

    /**
     * Este metodo permite borrar una seccion
     * @param id es el id de la seccion que queremos borrar
     * @return la seccion borrada, null si ocurre algun error
     */
    @Override
    public Seccion borrar(long id) {
        Optional<Seccion> seccion = seccionRepository.findById(id);
        if(seccion.isEmpty()){
            return null;
        }else{
            Seccion deleteSeccion = seccion.get();
            seccionRepository.delete(deleteSeccion);
            return deleteSeccion;
        }
    }

    /**
     * Este metodo permite modificar una seccion
     * @param newSeccion es la seccion con los nuevos datos
     * @return la seccion modificada, null si ocurre algun error
     */
    @Override
    public Seccion actualizar(Seccion newSeccion) {
        Optional<Seccion> optionalSeccion = seccionRepository.findById(newSeccion.getId());
        if(optionalSeccion.isEmpty()){
            return null;
        }else{
            Seccion seccion = optionalSeccion.get();
            seccion.setNombre(newSeccion.getNombre());
            seccion.setDescripcion(newSeccion.getDescripcion());
            return seccionRepository.save(seccion);
        }
    }

    /**
     * Este metodo permite obtener todos los nombres de las secciones
     * @return una lista con los nombres de las secciones
     */
    @Override
    public List<String> obtenerNombres() {
        return seccionRepository.obtenerNombres();
    }
}
