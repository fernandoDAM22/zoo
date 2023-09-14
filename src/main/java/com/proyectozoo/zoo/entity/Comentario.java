package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "comentarios")
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_animal")
    private long idAnimal;

    @Column(name = "id_usuario")
    private long idUsuario;

    @Column(name = "comentario")
    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 1,message = "El comentario no puede estar vacio")
    private String comentario;


    @Column(name = "fecha")
    private LocalDate fecha;
}
