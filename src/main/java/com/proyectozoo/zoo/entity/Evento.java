package com.proyectozoo.zoo.entity;

import jakarta.validation.constraints.*;
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

    @NotBlank(message = "error.evento.nombre")
    @Size(min = 4, message = "error.evento.longitud_nombre")
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha")
    @Future(message = "error.evento.fecha")
    private LocalDateTime fecha;

    @Column(name = "numero_plazas")
    @Min(value = 200,message = "error.evento.minimo_plazas")
    @Max(value = 2000, message = "error.evento.maximo_plazas")
    private int numeroPlazas;

    @Column(name = "id_seccion")
    private long idSeccion;

    @Column(name = "foto")
    private String foto;
}
