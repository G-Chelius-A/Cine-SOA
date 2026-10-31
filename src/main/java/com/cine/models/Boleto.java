package com.cine.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// CORRECCIÓN [34]: Entidad Boleto con folio (UUID PK String), filaAsiento/numeroAsiento Integer, cliente y funcion obligatorios, puntosGenerados y restricción única de asiento por función | MOTIVO: Modelo de referencia Boleto, Inconsistencias conocidas y Restricción semántica 6 | ANTES: id Long autoincremental, fila String, getNumero sumaba 1, faltaba puntosGenerados, sin restricción única compuesta
@Entity
@Table(name = "boletos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_boleto_funcion_asiento", columnNames = {"id_funcion", "fila_asiento", "numero_asiento"})
})
public class Boleto {

    // CORRECCIÓN [35]: folio como PK sustituta de tipo UUID almacenada como String | MOTIVO: Naturaleza de identificadores: Boleto.folio clave sustituta (UUID como String, no nulo) | ANTES: @Id @GeneratedValue private Long id; private String folio;
    @Id
    @Column(name = "folio", nullable = false, length = 36)
    private String folio;

    // CORRECCIÓN [36]: filaAsiento como Integer no nulo y positivo | MOTIVO: Modelo de referencia: filaAsiento (Integer, no nulo) e Inconsistencia resuelta (corregir filaAsientao y tipo String) | ANTES: private String fila;
    @NotNull(message = "La filaAsiento es obligatoria")
    @Positive(message = "La filaAsiento debe ser un entero positivo")
    @Column(name = "fila_asiento", nullable = false)
    private Integer filaAsiento;

    // CORRECCIÓN [37]: numeroAsiento como Integer no nulo y positivo | MOTIVO: Modelo de referencia: numeroAsiento (Integer, no nulo) | ANTES: private int numero; con getNumero() devolviendo numero + 1
    @NotNull(message = "El numeroAsiento es obligatorio")
    @Positive(message = "El numeroAsiento debe ser un entero positivo")
    @Column(name = "numero_asiento", nullable = false)
    private Integer numeroAsiento;

    // CORRECCIÓN [38]: Relación con Funcion (no nula) | MOTIVO: Asociaciones y cardinalidad: Funcion 1 : N Boleto con idFuncion no nulo | ANTES: @ManyToOne @JoinColumn(name = "funcion_id") sin nullable=false
    @NotNull(message = "La función es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_funcion", nullable = false)
    private Funcion funcion;

    // CORRECCIÓN [39]: Relación con Cliente (no nula) | MOTIVO: Inconsistencias conocidas: incluir idCliente como FK no nula en Boleto | ANTES: @ManyToOne @JoinColumn(name = "cliente_id") sin nullable=false
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    // AGREGADO [40]: puntosGenerados como Integer no nulo | MOTIVO: Modelo de referencia Boleto: puntosGenerados (Integer, no nulo)
    @Column(name = "puntos_generados", nullable = false)
    private Integer puntosGenerados;

    public Boleto() {
    }

    public Boleto(String folio, Integer filaAsiento, Integer numeroAsiento, Funcion funcion, Cliente cliente, Integer puntosGenerados) {
        this.folio = folio;
        this.filaAsiento = filaAsiento;
        this.numeroAsiento = numeroAsiento;
        this.funcion = funcion;
        this.cliente = cliente;
        this.puntosGenerados = puntosGenerados;
    }

    // CORRECCIÓN [41]: Getter directo de folio | MOTIVO: Corrección de bug donde getFolio() retornaba id.toString() | ANTES: public String getFolio() { return id == null ? null : id.toString(); }
    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getFilaAsiento() {
        return filaAsiento;
    }

    public void setFilaAsiento(Integer filaAsiento) {
        this.filaAsiento = filaAsiento;
    }

    // CORRECCIÓN [42]: Getter de numeroAsiento retornando el valor real sin sumar 1 | MOTIVO: Corrección de bug donde getNumero() sumaba 1 | ANTES: public int getNumero() { return numero + 1; }
    public Integer getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(Integer numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getPuntosGenerados() {
        return puntosGenerados;
    }

    public void setPuntosGenerados(Integer puntosGenerados) {
        this.puntosGenerados = puntosGenerados;
    }
}
