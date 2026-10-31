package com.cine.repositories;

import com.cine.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CORRECCIÓN [46]: ClienteRepository con String como tipo de PK (idCliente) y métodos corregidos | MOTIVO: Naturaleza de identificadores: Cliente.idCliente es String clave natural | ANTES: JpaRepository<Cliente, Long> y métodos con typo findByEmaill, findByPoints
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {

    // CORRECCIÓN [47]: Búsqueda de cliente por email | MOTIVO: Modelo de referencia: email único de cliente | ANTES: Existían métodos erróneos como findByEmaill
    Optional<Cliente> findByEmail(String email);

    // AGREGADO [48]: Búsqueda de cliente por numeroCliente | MOTIVO: numeroCliente es columna única autoincremental
    Optional<Cliente> findByNumeroCliente(Integer numeroCliente);

    // AGREGADO [113]: Consulta para obtener el número máximo actual de cliente | MOTIVO: Generar secuencialmente numeroCliente al registrar un nuevo cliente sin violar la especificación JPA de @GeneratedValue fuera de @Id
    @Query("SELECT COALESCE(MAX(c.numeroCliente), 0) FROM Cliente c")
    Integer findMaxNumeroCliente();
}
