package com.proyectozoo.zoo.entity;

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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "error.usuario.nombre")
    @Column(name = "nombre", unique = true)
    @Size(min = 1, message = "error.usuario.nombre_vacio")
    private String nombre;

    @NotBlank(message = "error.usuario.email_vacio")
    @Email(message = "error.usuario.email")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "error.usuario.contrasena")
    @Size(min = 8,message = "error.usuario.longitud_contrasena")
    @Column(name = "password")
    private String password;

    @Column(name = "foto")
    private String foto;

    @Column(name = "tipo")
    private String tipo;
}
