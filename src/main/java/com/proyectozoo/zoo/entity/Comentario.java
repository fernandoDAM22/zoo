package com.proyectozoo.zoo.entity;

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
    @NotBlank(message = "error.comentario.comentario")
    @Size(min = 1,message = "error.comentario.comentario_vacio")
    private String comentario;

    @Column(name = "fecha")
    @CreatedDate
    private LocalDate fecha;
}
