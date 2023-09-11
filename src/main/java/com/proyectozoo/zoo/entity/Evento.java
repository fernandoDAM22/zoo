package com.proyectozoo.zoo.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha")
    private LocalDateTime fecha;
    @Column(name = "numero_plazas")
    private int numeroPlazas;
    @Column(name = "id_seccion")
    private long idSeccion;
    @Column(name = "foto")
    private String foto;
}
