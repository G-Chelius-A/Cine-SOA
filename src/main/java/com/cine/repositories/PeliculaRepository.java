package com.cine.repositories;

import com.cine.models.Clasificacion;
import com.cine.models.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// CORRECCIÓN [45]: PeliculaRepository utilizando String como tipo de PK (idPelicula) | MOTIVO: Naturaleza de identificadores: Pelicula.idPelicula es String clave natural | ANTES: JpaRepository<Pelicula, Long> y métodos con typo findByGeneroo, findByClasificacionNombre
@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, String> {

    List<Pelicula> findByGenero(String genero);

    List<Pelicula> findByClasificacion(Clasificacion clasificacion);
}
