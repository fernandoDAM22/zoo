package com.proyectozoo.zoo.service.impl;

import com.proyectozoo.zoo.entity.Evento;
import com.proyectozoo.zoo.repository.EventoRepository;
import com.proyectozoo.zoo.service.IEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventoServiceImpl implements IEventoService {
    /**
     * Instancia del repositorio para poder acceder a los datos
     */
    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Este metodo permite obtener todos los eventos de la base de datos
     * @return una lista con todos los eventos de la base de datos
     */
    public List<Evento> obtener(){
        return eventoRepository.findAll();
    }

    /**
     * Este metodo permite obtener un evento por su id
     * @param id es el id del evento que queremos obtener
     * @return el evento con el id indicado si existe, null si no
     */
    public Evento buscarPorId(Long id){
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.orElse(null);
    }

    /**
     * Este metodo permite obtener un evento por su nombre
     * @param nombre es el nombre del evento que queremos obtener
     * @return el evento con ese nombre si existe, null si no existe
     */
    public Evento buscarPorNombre(String nombre){
        Optional<Evento> evento = eventoRepository.buscarPorNombre(nombre);
        return evento.orElse(null);
    }

    /**
     * Este metodo permite obtener todos los eventos de una seccion
     * @param id es el id de la seccion de la que queremos obtener los eventos
     * @return una lista con los eventos de esa seccion
     */
    public List<Evento> obtenerEventosPorSeccion(Long id){
        return eventoRepository.obtenerEventosPorSeccion(id);
    }

    /**
     * Este metodo permite guardar un evento en la base de datos
     * @param evento es el evento que vamos a guardar en la base de datos
     * @return el evento guardado
     */
    public Evento guardar(Evento evento){
        return eventoRepository.save(evento);
    }

    /**
     * Este metodo permite borrar un evento de la base de datos
     * @param id es el id del evento que queremos borrar
     * @return el evento borrado
     */
    public Evento borrar(Long id){
        Optional<Evento> evento = eventoRepository.findById(id);
        if(evento.isEmpty()){
            return null;
        }
        eventoRepository.delete(evento.get());
        return evento.get();
    }

    /**
     * Este metodo permite modificar un evento de la base de datos
     * @param newEvento es el evento con los nuevos datos
     * @return el evento moficicado con los nuevos datos
     */
    @Override
    public Evento modificar(Evento newEvento){
        Optional<Evento> optionalEvento = eventoRepository.findById(newEvento.getId());
        if(optionalEvento.isEmpty()){
            return null;
        }
        Evento evento = optionalEvento.get();
        evento.setNombre(newEvento.getNombre());
        evento.setFecha(newEvento.getFecha());
        evento.setIdSeccion(newEvento.getIdSeccion());
        evento.setNumeroPlazas(newEvento.getNumeroPlazas());
        eventoRepository.save(evento);
        return evento;
    }
}
