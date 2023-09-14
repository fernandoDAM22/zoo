package com.proyectozoo.zoo.entity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha")
    @Future(message = "La fecha no puede ser anterior ni igual al dia actual")
    private LocalDateTime fecha;

    @Column(name = "numero_plazas")
    @Min(value = 200,message = "El numero minimo de plazas para un evento es 200")
    @Max(value = 2000, message = "El numero maximo de plazas para un evento es 2000")
    private int numeroPlazas;

    @Column(name = "id_seccion")
    private long idSeccion;

    @Column(name = "foto")
    private String foto;
}
