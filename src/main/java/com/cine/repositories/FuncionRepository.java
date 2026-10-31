package com.cine.repositories;

import com.cine.models.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

// CORRECCIÓN [49]: FuncionRepository con String como PK (idFuncion UUID) y corrección de nombres de métodos | MOTIVO: Naturaleza de identificadores: Funcion.idFuncion es UUID como String | ANTES: JpaRepository<Funcion, Long> con métodos con typos como findByFechaHoraBetwen, findBySalaNumer, findByFechaHorra
@Repository
public interface FuncionRepository extends JpaRepository<Funcion, String> {

    List<Funcion> findByPeliculaIdPelicula(String idPelicula);

    List<Funcion> findBySalaNumeroSala(Long numeroSala);

    List<Funcion> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
