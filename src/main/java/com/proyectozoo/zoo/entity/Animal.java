package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
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
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "especie")
    @NotBlank(message = "La especie es obligatoria")
    private String especie;
    @Column(name = "fecha_nacimiento")
    @Past(message = "La fecha de nacimiento no puede ser igual o mayor a la fecha actual")
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
