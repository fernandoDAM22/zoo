package com.proyectozoo.zoo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre", unique = true)
    private String nombre;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    @Column(name = "email", unique = true)
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "password")
    private String password;
    @Column(name = "foto")
    private String foto;
    @Column(name = "tipo")
    private String tipo;
}
