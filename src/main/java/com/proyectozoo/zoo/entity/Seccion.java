package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, message = "El nombre no puede estar vacio")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "foto")
    private String foto;
}
