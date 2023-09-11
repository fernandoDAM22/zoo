package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
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
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "especie")
    private String especie;
    @Column(name = "fecha_nacimiento")
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
