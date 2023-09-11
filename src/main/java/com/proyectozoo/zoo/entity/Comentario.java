package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
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
    private String comentario;
    @Column(name = "fecha")
    private LocalDate fecha;
}
