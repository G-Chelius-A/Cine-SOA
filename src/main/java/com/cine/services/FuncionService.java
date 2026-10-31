package com.cine.services;

import com.cine.exceptions.ResourceNotFoundException;
import com.cine.models.Funcion;
import com.cine.models.Pelicula;
import com.cine.models.Sala;
import com.cine.repositories.FuncionRepository;
import com.cine.repositories.PeliculaRepository;
import com.cine.repositories.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// CORRECCIÓN [78]: FuncionService con inyección por constructor y generación de UUID sin DTOs | MOTIVO: Restricción semántica 4 (BigDecimal) y Naturaleza de identificadores: Funcion.idFuncion UUID String | ANTES: Inyección @Autowired por campo, obtener() retornaba null, programar() seteaba precio en 0 y usaba Long id
@Service
public class FuncionService {

    // CORRECCIÓN [79]: Inyección por constructor en lugar de @Autowired por campo |
    // MOTIVO: Buenas prácticas de diseño de software y testeabilidad | ANTES:
    // @Autowired por campo para FuncionRepository, PeliculaRepository y
    // SalaRepository
    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;

    public FuncionService(FuncionRepository funcionRepository,
            PeliculaRepository peliculaRepository,
            SalaRepository salaRepository) {
        this.funcionRepository = funcionRepository;
        this.peliculaRepository = peliculaRepository;
        this.salaRepository = salaRepository;
    }

    // CORRECCIÓN [80]: listar() retorna las funciones con su Película y Sala |
    // MOTIVO: Modelo de referencia Funcion | ANTES: funcionRepository.findAll() con
    // entidades desnudas
    @Transactional(readOnly = true)
    public List<Funcion> listar() {
        return funcionRepository.findAll();
    }

    // CORRECCIÓN [81]: obtener() busca por idFuncion (UUID String) | MOTIVO:
    // Naturaleza de identificadores Funcion (UUID String) y corrección de bug donde
    // retornaba null | ANTES: public Funcion obtener(Long id) { return null; }
    @Transactional(readOnly = true)
    public Funcion obtener(String idFuncion) {
        return funcionRepository.findById(idFuncion)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + idFuncion));
    }

    // CORRECCIÓN [82]: programar() genera UUID para idFuncion, valida existencia de
    // Pelicula y Sala, y respeta el precio | MOTIVO: Modelo de referencia Funcion
    // (idFuncion UUID String, precio BigDecimal) | ANTES: Recibía entidad desnuda,
    // sobreescribía funcion.setPrecio(0)
    @Transactional
    public Funcion programar(String idPelicula, Long numeroSala, LocalDateTime fechaHora, BigDecimal precio) {
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con ID: " + idPelicula));

        Sala sala = salaRepository.findById(numeroSala)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con número: " + numeroSala));

        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora de la función es obligatoria");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        String nuevoIdFuncion = UUID.randomUUID().toString();
        Funcion funcion = new Funcion(
                nuevoIdFuncion,
                pelicula,
                sala,
                fechaHora,
                precio);

        return funcionRepository.save(funcion);
    }
}
