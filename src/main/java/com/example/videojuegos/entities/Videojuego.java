package com.example.videojuegos.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "videojuegos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Videojuego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "El titulo no puede venir vacío")
    private String titulo;
    @Size(min = 5, max = 100, message = "La descripción debe ser entre 5 y 100 caracteres")
    private String descripcion;

    private String imagen;

    @Min(value = 5, message = "El precio debe ser como mínimo 5")
    @Max(value = 10000, message = "Precio muy alto")
    private float precio;
    @Min(value = 1, message = "Minimo de stock permitido: 1")
    @Max(value = 10000, message = "Stock muy alto")
    private short stock;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "No puede ser nula")
    @PastOrPresent(message = "Debe ser igual o menor a la fecha de hoy")
    private Date fechaLanzamiento;
    private boolean activo = true;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_estudio", nullable = false)
    @NotNull(message = "Es requerido el estudio")
    private Estudio estudio;

    @NotNull(message = "Es requerida la categoría")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_categoria", nullable = false)
    private Categoria categoria;
}
