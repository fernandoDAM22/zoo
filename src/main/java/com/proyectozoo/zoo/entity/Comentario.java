package com.proyectozoo.zoo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "comentarios")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Entidad que representa un Comentario")
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Id del comentario", example = "1")
    private Long id;

    @Column(name = "id_animal")
    @Schema(description = "Id del animal", example = "8")
    private long idAnimal;

    @Column(name = "id_usuario")
    @Schema(description = "Id del usuario", example = "5")
    private long idUsuario;

    @Column(name = "comentario")
    @NotBlank(message = "error.comentario.comentario")
    @Size(min = 1,message = "error.comentario.comentario_vacio")
    @Schema(description = "Texto del comentario", example = "Muy bonito el animal")
    private String comentario;

    @Column(name = "fecha")
    @CreatedDate
    @Schema(description = "Fecha de publicacion del comentario", example = "2023/09/21")
    private LocalDate fecha;
}
