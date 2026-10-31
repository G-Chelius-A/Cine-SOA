package com.cine.repositories;

import com.cine.models.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CORRECCIÓN [43]: Repositorio SalaRepository usando Long como tipo de PK | MOTIVO: Sala.numeroSala es PK de tipo Long | ANTES: Métodos con typos como findByNumeroo y findByCapacidadDisponible
@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    // AGREGADO [44]: Búsqueda de sala por numeroSala | MOTIVO: Facilitar la consulta semántica por número de sala
    Optional<Sala> findByNumeroSala(Long numeroSala);
}
