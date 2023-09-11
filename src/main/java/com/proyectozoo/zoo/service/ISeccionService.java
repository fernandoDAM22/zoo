package com.proyectozoo.zoo.service;

import com.proyectozoo.zoo.entity.Seccion;
import com.proyectozoo.zoo.repository.SeccionRepository;

import java.util.List;

public interface ISeccionService {
    List<Seccion> obtenerSecciones();
    Seccion buscarPorId(long id);
    Seccion buscarPorNombre(String nombre);
    Seccion guardar(Seccion seccion);
    Seccion borrar(long id);
    Seccion actualizar(Seccion newSeccion);
    List<String> obtenerNombres();
}
