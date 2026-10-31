package com.cine.services;

import com.cine.exceptions.ResourceNotFoundException;
import com.cine.models.Clasificacion;
import com.cine.models.Pelicula;
import com.cine.repositories.PeliculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// CORRECCIÓN [67]: PeliculaService con inyección por constructor y manejo de clave natural String con entidad Pelicula | MOTIVO: Naturaleza de identificadores: Pelicula.idPelicula clave natural String y eliminación de DTOs | ANTES: Inyección @Autowired por campo, findById(Long), porClasificacion retornaba siempre Clasificacion.A y listar() retornaba lista vacía
@Service
public class PeliculaService {

    // CORRECCIÓN [68]: Inyección por constructor en lugar de @Autowired por campo | MOTIVO: Buenas prácticas de diseño de software y testeabilidad | ANTES: @Autowired private PeliculaRepository peliculaRepository;
    private final PeliculaRepository peliculaRepository;

    public PeliculaService(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    // CORRECCIÓN [69]: listar() retorna las películas reales de la base de datos | MOTIVO: Corrección de bug donde listar() retornaba lista vacía | ANTES: public List<Pelicula> listar() { return List.of(); }
    @Transactional(readOnly = true)
    public List<Pelicula> listar() {
        return peliculaRepository.findAll();
    }

    // CORRECCIÓN [70]: obtener() recibe String idPelicula y lanza ResourceNotFoundException si no existe | MOTIVO: Naturaleza de identificadores: idPelicula String | ANTES: public Pelicula obtener(Long id) { return peliculaRepository.findById(id).get(); }
    @Transactional(readOnly = true)
    public Pelicula obtener(String idPelicula) {
        return peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con ID: " + idPelicula));
    }

    // CORRECCIÓN [71]: porClasificacion() filtra por la clasificación recibida | MOTIVO: Corrección de bug donde filtraba fijamente por Clasificacion.A | ANTES: return peliculaRepository.findByClasificacion(Clasificacion.A);
    @Transactional(readOnly = true)
    public List<Pelicula> porClasificacion(Clasificacion clasificacion) {
        return peliculaRepository.findByClasificacion(clasificacion);
    }

    // CORRECCIÓN [72]: crear() guarda la película con validación de ID no existente | MOTIVO: Clave natural no debe duplicarse | ANTES: peliculaRepository.save(pelicula) sin validar
    @Transactional
    public Pelicula crear(Pelicula pelicula) {
        if (peliculaRepository.existsById(pelicula.getIdPelicula())) {
            throw new IllegalArgumentException("Ya existe una película con ID: " + pelicula.getIdPelicula());
        }
        return peliculaRepository.save(pelicula);
    }
}
