package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "animales")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "error.animal.nombre")
    @Column(name = "nombre",unique = true)
    @Size(min = 4, message = "error.animal.longitud_nombre")
    private String nombre;

    @Column(name = "especie")
    @NotBlank(message = "error.animal.especie")
    @Size(min = 4, message = "error.animal.longitud_especie")
    private String especie;

    @Column(name = "fecha_nacimiento")
    @PastOrPresent(message = "error.animal.fecha")
    private Date fechaNacimiento;

    @Column(name = "curiosidad")
    private String curiosidad;

    @Column(name = "sexo")
    private char sexo;

    @Column(name = "foto")
    private String foto;

    @Column(name = "id_seccion")
    private long idSeccion;
}
