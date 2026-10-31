package com.cine.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// CORRECCIÓN [2]: Entidad Sala con numeroSala como PK autoincremental y capacidad | MOTIVO: Modelo de referencia Sala (numeroSala PK, Long, autoincremental, no nulo; capacidad Integer, no nulo) y Bean Validation en modelo | ANTES: Sala tenía id autoincremental y numero redundante, y getCapacidad() retornaba numero
@Entity
@Table(name = "salas")
public class Sala {

    // CORRECCIÓN [3]: numeroSala como PK autoincremental de tipo Long | MOTIVO: Modelo de referencia: Sala.numeroSala (PK, Long, autoincremental, no nulo) | ANTES: private Long id; private Integer numero;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_sala", nullable = false)
    private Long numeroSala;

    // CORRECCIÓN [4]: capacidad como Integer no nulo y validación positiva | MOTIVO: Modelo de referencia: capacidad (Integer, no nulo) | ANTES: private Integer capacidad sin @Column(nullable = false) y con error en constructor
    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    public Sala() {
    }

    // CORRECCIÓN [5]: Constructor asignando correctamente numeroSala y capacidad | MOTIVO: Corrección de error de lógica donde this.numero = capacidad y this.capacidad = numero | ANTES: this.numero = capacidad; this.capacidad = numero;
    public Sala(Long numeroSala, Integer capacidad) {
        this.numeroSala = numeroSala;
        this.capacidad = capacidad;
    }

    public Sala(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Long getNumeroSala() {
        return numeroSala;
    }

    public void setNumeroSala(Long numeroSala) {
        this.numeroSala = numeroSala;
    }

    // CORRECCIÓN [6]: Getter de capacidad retornando el atributo capacidad | MOTIVO: Corrección de bug donde getCapacidad() retornaba el número de sala | ANTES: public Integer getCapacidad() { return numero; }
    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }
}
