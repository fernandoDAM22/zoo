package com.proyectozoo.zoo.service.impl;

import com.proyectozoo.zoo.entity.Animal;
import com.proyectozoo.zoo.repository.AnimalRepository;
import com.proyectozoo.zoo.service.IAnimalService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalServiceImpl implements IAnimalService {
    /**
     * Instancia del repositorio para poder acceder a los datos
     */
    @Autowired
    private AnimalRepository animalRepository;

    /**
     * Este metodo permite obtener todos los animales de la base de datos
     *
     * @return una lista con todos los animales de la base de datos
     */
    @Override
    public List<Animal> obtenerAnimales() {
        return animalRepository.findAll();
    }

    /**
     * Este metodo permite obtener todos los animales de una seccion de la base de datos
     *
     * @param id es el id de la seccion
     * @return una lista con todos los animales de esa seccion
     */
    @Override
    public List<Animal> obtenerAnimalesPorSeccion(Long id) {
        return animalRepository.obtenerAnimalesPorSeccion(id);
    }

    /**
     * Este metodo permite obtener un animal por su id
     *
     * @param id es el id del animal que queremos obtener
     * @return el animal con ese id si existe, null si no existe
     */

    @Override
    public Animal buscarPorId(Long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        return animal.orElse(null);
    }

    /**
     * Este metodo permite buscar a un animal por su nombre
     *
     * @param nombre es el del animal que queremos obtener
     * @return el animal con ese nombre si existe o null si no existe
     */
    @Override
    public Animal buscarPorNombre(String nombre) {
        Optional<Animal> animal = animalRepository.obtenerPorNombre(nombre);
        return animal.orElse(null);
    }

    /**
     * Este metodo permite guardar a un animal en la base de datos
     *
     * @param animal es el animal que queremos guardar en la base de datos
     * @return el animal guardado en la base de datos
     */
    @Override
    public Animal guardar(Animal animal) {
        return animalRepository.save(animal);
    }

    /**
     * Este meotodo permite borrar a un animal de la base de datos
     *
     * @param id es el id del animal que queremos borrar
     * @return el animal borrado de la base de datos
     */
    @Override
    public Animal borrar(Long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        if (animal.isEmpty()) {
            return null;
        }
        Animal deleteAnimal = animal.get();
        animalRepository.delete(deleteAnimal);
        return deleteAnimal;
    }

    /**
     * Este metodo permite actualizar un animal de la base de datos a excepcion de su foto
     *
     * @param newAnimal es el animal con los nuevos datos
     * @return el animal modificiado con los nuevos datos asignados
     */
    @Override
    public Animal actualizar(Animal newAnimal) {
        Optional<Animal> optionalAnimal = animalRepository.findById(newAnimal.getId());
        if (optionalAnimal.isEmpty()) {
            return null;
        }
        Animal animal = optionalAnimal.get();
        animal.setNombre(newAnimal.getNombre());
        animal.setEspecie(newAnimal.getEspecie());
        animal.setCuriosidad(newAnimal.getCuriosidad());
        animal.setSexo(newAnimal.getSexo());
        animal.setIdSeccion(animal.getIdSeccion());
        animalRepository.save(animal);
        return animal;
    }

    /**
     * Este metodo permite obtener el animal con mas comentarios en la ultima semana
     *
     * @return el animal con mas comentarios en la ultima semana
     */
    @Override
    public Animal animalMasVotadoSemana() {
        return animalRepository.animalMasPopularSemana();
    }

    /**
     * Este metodo permite obtener el animal mas comentarios en el ultimo mes
     *
     * @return el animal con mas comentarios en el ultimo mes
     */
    @Override
    public Animal animalMasVotadoMes() {
        return animalRepository.animalMasPopularMes();
    }
}
