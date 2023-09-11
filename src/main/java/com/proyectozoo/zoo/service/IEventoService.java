package com.proyectozoo.zoo.service;

import com.proyectozoo.zoo.entity.Evento;

import java.util.List;

public interface IEventoService {
    List<Evento> obtener();
    Evento buscarPorId(Long id);
    Evento buscarPorNombre(String nombre);
    List<Evento> obtenerEventosPorSeccion(Long id);
    Evento guardar(Evento evento);
    Evento borrar(Long id);
    Evento modificar(Evento newEvento);

}
