package com.cine.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// CORRECCIÓN [26]: Entidad Funcion con idFuncion (UUID String), precio con BigDecimal(10,2), relaciones no nulas y getFechaHora real | MOTIVO: Modelo de referencia Funcion: idFuncion (PK, UUID como String, no nulo), fechaHora (no nulo), precio (BigDecimal, no nulo), idSala (Long, FK), idPelicula (String, FK) y Restricción semántica 4 (BigDecimal) | ANTES: id Long autoincremental, precio double con * 100, getFechaHora retornaba LocalDateTime.now()
@Entity
@Table(name = "funciones")
public class Funcion {

    // CORRECCIÓN [27]: idFuncion como PK sustituta de tipo UUID almacenada como String | MOTIVO: Naturaleza de identificadores: Funcion.idFuncion clave sustituta (UUID como String, no nulo) | ANTES: @Id @GeneratedValue private Long id;
    @Id
    @Column(name = "id_funcion", nullable = false, length = 36)
    private String idFuncion;

    // CORRECCIÓN [28]: Relacion pelicula con JoinColumn id_pelicula | MOTIVO: Asociaciones y cardinalidad 2: Pelicula 1 : N Funcion (una función proyecta una sola película) con id_pelicula FK | ANTES: @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pelicula_id") sin nullable=false
    @NotNull(message = "La película es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_pelicula", nullable = false)
    private Pelicula pelicula;

    // CORRECCIÓN [29]: Relacion sala con JoinColumn id_sala | MOTIVO: Asociaciones y cardinalidad 1: Sala 1 : N Funcion (una función pertenece a una sola sala) con id_sala FK | ANTES: @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sala_id") sin nullable=false
    @NotNull(message = "La sala es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    // CORRECCIÓN [30]: fechaHora como LocalDateTime no nulo | MOTIVO: Modelo de referencia: fechaHora (LocalDateTime, no nulo) | ANTES: private LocalDateTime fechaHora; con getFechaHora() que retornaba LocalDateTime.now()
    @NotNull(message = "La fecha y hora de la función es obligatoria")
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // CORRECCIÓN [31]: precio como BigDecimal con precision 10 y scale 2 no nulo | MOTIVO: Restricción semántica 4: Todo valor monetario usa BigDecimal con precisión y escala explícitas | ANTES: private double precio; con getPrecio() multiplicando por 100
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @JsonIgnore
    @OneToMany(mappedBy = "funcion", cascade = CascadeType.ALL)
    private List<Boleto> boletos = new ArrayList<>();

    public Funcion() {
    }

    public Funcion(String idFuncion, Pelicula pelicula, Sala sala, LocalDateTime fechaHora, BigDecimal precio) {
        this.idFuncion = idFuncion;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precio = precio;
    }

    public String getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(String idFuncion) {
        this.idFuncion = idFuncion;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    // CORRECCIÓN [32]: getFechaHora() retorna el atributo real | MOTIVO: Corrección de bug crítico donde getFechaHora() retornaba LocalDateTime.now() | ANTES: public LocalDateTime getFechaHora() { return LocalDateTime.now(); }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    // CORRECCIÓN [33]: getPrecio() retorna el BigDecimal real sin operaciones aritméticas indebidas | MOTIVO: Restricción semántica 4 y corrección de bug | ANTES: public double getPrecio() { return precio * 100; }
    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<Boleto> boletos) {
        this.boletos = boletos;
    }
}
