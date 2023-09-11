package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "secciones")
public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "foto")
    private String foto;
}
