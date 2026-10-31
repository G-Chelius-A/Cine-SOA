package com.cine.services;

import com.cine.exceptions.ResourceNotFoundException;
import com.cine.models.Sala;
import com.cine.repositories.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// CORRECCIÓN [66]: Servicio SalaService con inyección por constructor y CRUD sobre entidad Sala | MOTIVO: Arquitectura sin DTOs: operar directamente con modelos y mantener buenas prácticas | ANTES: SalaService utilizaba SalaRequestDTO y SalaResponseDTO
@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Transactional(readOnly = true)
    public List<Sala> listar() {
        return salaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Sala obtenerPorNumero(Long numeroSala) {
        return salaRepository.findById(numeroSala)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con número: " + numeroSala));
    }

    @Transactional
    public Sala crear(Sala sala) {
        return salaRepository.save(sala);
    }
}
