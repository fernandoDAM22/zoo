package com.proyectozoo.zoo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "eventos")
@Schema(description = "Entidad que representa un evento")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Id del evento", example = "1")
    private Long id;

    @NotBlank(message = "error.evento.nombre")
    @Size(min = 4, message = "error.evento.longitud_nombre")
    @Column(name = "nombre")
    @Schema(description = "Nombre del evento", example = "Leon nadador")
    private String nombre;

    @Column(name = "fecha")
    @Future(message = "error.evento.fecha")
    @Schema(description = "Fecha del evento", example = "2023/09/21T18:00:00")
    private LocalDateTime fecha;

    @Column(name = "numero_plazas")
    @Min(value = 200,message = "error.evento.minimo_plazas")
    @Max(value = 2000, message = "error.evento.maximo_plazas")
    @Schema(description = "Numero de plazas del evento", example = "1000")
    private int numeroPlazas;

    @Column(name = "id_seccion")
    @Schema(description = "Id de la seccion del evento", example = "5")
    private long idSeccion;

    @Column(name = "foto")
    @Schema(description = "Foto del evento", example = "C:/imagenes/eventos/leon_nadador.jpg")
    private String foto;
}
