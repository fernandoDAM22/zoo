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
    @NotBlank(message = "error.seccion.nombre")
    @Size(min = 4, message = "error.seccion.longitud_nombre")
    private String nombre;

    @Column(name = "descripcion")
    @Size(min = 10,message = "error.seccion.longitud_descripcion")
    @NotBlank(message = "error.seccion.descripcion")
    private String descripcion;

    @Column(name = "foto")
    private String foto;
}
