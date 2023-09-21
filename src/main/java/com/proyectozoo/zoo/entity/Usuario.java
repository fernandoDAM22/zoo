package com.proyectozoo.zoo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nombre"),
        @UniqueConstraint(columnNames = "email")
})
@Schema(description = "Entidad que representa un usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Id del usuario", example = "1")
    private Long id;

    @NotBlank(message = "error.usuario.nombre")
    @Column(name = "nombre", unique = true)
    @Size(min = 1, message = "error.usuario.nombre_vacio")
    @Schema(description = "Nombre del usuario", example = "Fernando")
    private String nombre;

    @NotBlank(message = "error.usuario.email_vacio")
    @Email(message = "error.usuario.email")
    @Column(name = "email", unique = true)
    @Schema(description = "Email del usuario", example = "fernando@gmail.com")
    private String email;

    @NotBlank(message = "error.usuario.contrasena")
    @Size(min = 8,message = "error.usuario.longitud_contrasena")
    @Column(name = "password")
    @Schema(description = "Contrasena del usuario", example = "fernando123")
    private String password;

    @Column(name = "foto")
    @Schema(description = "Foto del usuario", example = "C:/imagenes/usuarios/fernando.jpg")
    private String foto;

    @Column(name = "tipo")
    @Schema(description = "Tipo del usuario (USER|ADMIN)", example = "USER")
    private String tipo;
}
