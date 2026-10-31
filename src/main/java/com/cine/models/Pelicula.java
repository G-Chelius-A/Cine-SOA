package com.cine.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// CORRECCIÓN [7]: Entidad Pelicula con idPelicula (PK natural String), validaciones y atributos según modelo | MOTIVO: Modelo de referencia Pelicula: idPelicula (PK natural String), titulo (length=255, no nulo), sinopsis (length=255), clasificacion (Enum STRING, no nulo), duracionMinutos (Integer, no nulo), genero (no nulo) | ANTES: id Long autoincremental, duracion int, getTitulo retornaba null, @Enumerated ORDINAL
@Entity
@Table(name = "peliculas")
public class Pelicula {

    // CORRECCIÓN [8]: idPelicula como clave primaria natural no nula de tipo String
    // | MOTIVO: Naturaleza de identificadores: Pelicula.idPelicula clave natural
    // (p. ej. "AVE-B15-SU") | ANTES: @Id @GeneratedValue private Long id;
    // Nota: No se usa autogenerado debido a que el formato del ID es personalizado.
    // Por cuestiones de tiempo, no se ha construido ese formato automáticamente.
    @Id
    @NotBlank(message = "El idPelicula es obligatorio")
    @Size(max = 255, message = "El idPelicula no debe exceder 255 caracteres")
    @Column(name = "id_pelicula", nullable = false, length = 255)
    private String idPelicula;

    // CORRECCIÓN [9]: titulo no nulo con length=255 | MOTIVO: Modelo de referencia:
    // titulo (String, no nulo, length=255) | ANTES: private String titulo sin
    // restricciones en @Column
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no debe exceder 255 caracteres")
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    // AGREGADO [10]: Atributo sinopsis con length=255 | MOTIVO: Modelo de
    // referencia: sinopsis (String, length=255)
    @Size(max = 255, message = "La sinopsis no debe exceder 255 caracteres")
    @Column(name = "sinopsis", length = 255)
    private String sinopsis;

    // CORRECCIÓN [11]: clasificacion con @Enumerated(EnumType.STRING) no nula |
    // MOTIVO: Restricción semántica 1: enum con @Enumerated(EnumType.STRING) y no
    // nulo | ANTES: @Enumerated(EnumType.ORDINAL) private Clasificacion
    // clasificacion;
    @NotNull(message = "La clasificación es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "clasificacion", nullable = false, length = 10)
    private Clasificacion clasificacion;

    // CORRECCIÓN [12]: duracionMinutos como Integer no nulo y positivo | MOTIVO:
    // Restricción semántica 2 y Modelo de referencia: duracionMinutos (Integer, no
    // nulo) | ANTES: private int duracion;
    @NotNull(message = "La duración en minutos es obligatoria")
    @Positive(message = "La duración debe ser mayor a 0 minutos")
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    // CORRECCIÓN [13]: genero como String no nulo | MOTIVO: Modelo de referencia:
    // genero (String, no nulo) | ANTES: private String genero sin @Column(nullable
    // = false)
    @NotBlank(message = "El género es obligatorio")
    @Size(max = 255, message = "El género no debe exceder 255 caracteres")
    @Column(name = "genero", nullable = false, length = 255)
    private String genero;

    public Pelicula() {
    }

    public Pelicula(String idPelicula, String titulo, String sinopsis, Clasificacion clasificacion,
            Integer duracionMinutos, String genero) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.sinopsis = sinopsis;
        this.clasificacion = clasificacion;
        this.duracionMinutos = duracionMinutos;
        this.genero = genero;
    }

    public String getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(String idPelicula) {
        this.idPelicula = idPelicula;
    }

    // CORRECCIÓN [14]: getTitulo() retorna el valor real de titulo | MOTIVO:
    // Corrección de bug donde retornaba siempre null | ANTES: public String
    // getTitulo() { return null; }
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        this.clasificacion = clasificacion;
    }

    // CORRECCIÓN [15]: Getter y Setter para duracionMinutos | MOTIVO: Modelo de
    // referencia: duracionMinutos (Integer, no nulo) | ANTES: public int
    // getDuracion() / setDuracion
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
