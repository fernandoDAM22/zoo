package com.proyectozoo.zoo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "secciones")
@Schema(description = "Entidad que representa una seccion")
public class Seccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Id de la seccion", example = "1")
    private Long id;

    @Column(name = "nombre")
    @NotBlank(message = "error.seccion.nombre")
    @Size(min = 4, message = "error.seccion.longitud_nombre")
    @Schema(description = "Nombre de la seccion", example = "Pantano")
    private String nombre;

    @Column(name = "descripcion")
    @Size(min = 10,message = "error.seccion.longitud_descripcion")
    @NotBlank(message = "error.seccion.descripcion")
    @Schema(description = "Descripcion de la seccion", example = "Esta seccion contiene animales tipicos de los pantanos")
    private String descripcion;

    @Column(name = "foto")
    @Schema(description = "Foto de la seccion", example = "C:/imagenes/secciones/pantano.jpg")
    private String foto;
}
