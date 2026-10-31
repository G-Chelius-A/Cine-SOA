package com.cine.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

// CORRECCIÓN [16]: Entidad Cliente con idCliente como PK natural, numeroCliente con secuencia autoincremental única, email con @Email y fechaRegistro/activo | MOTIVO: Modelo de referencia Cliente e Inconsistencias Conocidas: idCliente (PK natural, no nulo), numeroCliente (autoincremental único, no PK), activo, fechaRegistro, puntos | ANTES: id Long autoincremental, email retornaba nombre, getPuntos retornaba puntos+1, registrado boolean en vez de activo
@Entity
@Table(name = "clientes")
public class Cliente {

    // CORRECCIÓN [17]: idCliente como clave natural String y PK | MOTIVO: Naturaleza de identificadores: Cliente.idCliente clave natural (PK, String, no nulo) | ANTES: @Id private Long id;
    @Id
    @NotBlank(message = "El idCliente es obligatorio")
    @Size(max = 100, message = "El idCliente no debe exceder 100 caracteres")
    @Column(name = "id_cliente", nullable = false, length = 100)
    private String idCliente;

    // CORRECCIÓN [18]: numeroCliente único autoincremental/secuencial (no PK) sin @GeneratedValue | MOTIVO: En JPA/Hibernate @GeneratedValue solo es legal en la propiedad del identificador (@Id); para columnas únicas no PK se gestiona a nivel de persistencia/servicio | ANTES: @GeneratedValue(strategy = GenerationType.IDENTITY) causaba Property 'numeroCliente' is annotated '@GeneratedValue' but is not part of an identifier
    @Column(name = "numero_cliente", nullable = false, unique = true)
    private Integer numeroCliente;

    // CORRECCIÓN [19]: nombre como String no nulo | MOTIVO: Modelo de referencia: nombre (String, no nulo) | ANTES: private String nombre sin @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no debe exceder 255 caracteres")
    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    // CORRECCIÓN [20]: email como String no nulo y único validado con @Email | MOTIVO: Restricción semántica 3: email validado con @Email (formato cadena@dominio.extensión) además de @NotBlank | ANTES: private String email; getEmail() retornaba this.nombre
    @NotBlank(message = "El email es obligatorio")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "El email debe tener un formato válido (cadena@dominio.extensión)")
    @Size(max = 255, message = "El email no debe exceder 255 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    // CORRECCIÓN [21]: activo como Boolean no nulo | MOTIVO: Modelo de referencia: activo (Boolean, no nulo) | ANTES: private boolean registrado;
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // AGREGADO [22]: fechaRegistro como LocalDateTime no nulo | MOTIVO: Modelo de referencia: fechaRegistro (LocalDateTime, no nulo)
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    // CORRECCIÓN [23]: puntos como Integer no nulo | MOTIVO: Modelo de referencia: puntos (Integer, no nulo) | ANTES: private Integer puntos; getPuntos() retornaba puntos + 1
    @Column(name = "puntos", nullable = false)
    private Integer puntos = 0;

    public Cliente() {
    }

    public Cliente(String idCliente, String nombre, String email, Boolean activo, LocalDateTime fechaRegistro, Integer puntos) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.activo = activo != null ? activo : true;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
        this.puntos = puntos != null ? puntos : 0;
    }

    public Cliente(String idCliente, String nombre, String email) {
        this(idCliente, nombre, email, true, LocalDateTime.now(), 0);
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.puntos == null) {
            this.puntos = 0;
        }
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(Integer numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // CORRECCIÓN [24]: getEmail() retorna el email real del cliente | MOTIVO: Corrección de bug crítico donde getEmail() retornaba el nombre | ANTES: public String getEmail() { return nombre; }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // CORRECCIÓN [25]: getPuntos() retorna los puntos reales sin sumar 1 | MOTIVO: Corrección de bug donde getPuntos() retornaba puntos + 1 | ANTES: public Integer getPuntos() { return puntos == null ? null : puntos + 1; }
    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }
}
