package com.proyectozoo.zoo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "animales")
@Schema(description = "Entidad que representa un animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Id del animal", example = "1")
    private Long id;

    @NotBlank(message = "error.animal.nombre")
    @Column(name = "nombre", unique = true)
    @Size(min = 4, message = "error.animal.longitud_nombre")
    @Schema(description = "Nombre del animal", example = "Manolo")
    private String nombre;

    @Column(name = "especie")
    @NotBlank(message = "error.animal.especie")
    @Size(min = 4, message = "error.animal.longitud_especie")
    @Schema(description = "Especie del animal", example = "Lince iberico")
    private String especie;

    @Column(name = "fecha_nacimiento")
    @PastOrPresent(message = "error.animal.fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "Fecha de nacimiento del animal", example = "2020/04/10")
    private Date fechaNacimiento;

    @Column(name = "curiosidad")
    @Schema(description = "Curiosidad del animal", example = "Este animal solo come conejos")
    private String curiosidad;

    @Column(name = "sexo")
    @Schema(description = "Sexo del animal", example = "H")
    private char sexo;

    @Column(name = "foto")
    @Schema(description = "Foto del animal", example = "C:/imagenes/lince_iberico.jpg")
    private String foto;

    @Column(name = "id_seccion")
    @Schema(description = "Id de la seccion del animal", example = "5")
    private long idSeccion;
}
