package com.proyectozoo.zoo.service;

import com.proyectozoo.zoo.entity.Animal;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IAnimalService {
    List<Animal> obtenerAnimales();
    List<Animal> obtenerAnimalesPorSeccion(Long id);
    Animal buscarPorId(Long id);
    Animal buscarPorNombre(String nombre);
    Animal guardar(Animal animal);
    Animal borrar(Long id);
    Animal actualizar(Animal newAnimal);
    Animal animalMasVotadoSemana();
    Animal animalMasVotadoMes();
}
