package com.cine.controllers;

import com.cine.models.Sala;
import com.cine.services.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CORRECCIÓN [86]: SalaController con inyección por constructor y métodos HTTP REST estándar operando con entidad Sala | MOTIVO: Tarea 3: Corregir malas prácticas, @PutMapping usado para creación y usar @PostMapping con @Valid y sin DTOs | ANTES: Inyección de SalaRepository directa con @Autowired, listar retornaba List.of(), @PutMapping para crear
@RestController
@RequestMapping("/salas")
public class SalaController {

    // CORRECCIÓN [87]: Inyección de SalaService por constructor | MOTIVO: Buenas
    // prácticas de arquitectura por capas (Controller -> Service -> Repository) |
    // ANTES: @Autowired private SalaRepository salaRepository;
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    // CORRECCIÓN [88]: Listar salas reales a través del servicio | MOTIVO:
    // Corrección de bug donde retornaba lista vacía | ANTES: public List<Sala>
    // listar() { return List.of(); }
    @GetMapping
    public ResponseEntity<List<Sala>> listar() {
        return ResponseEntity.ok(salaService.listar());
    }

    @GetMapping("/{numeroSala}")
    public ResponseEntity<Sala> obtenerPorNumero(@PathVariable Long numeroSala) {
        return ResponseEntity.ok(salaService.obtenerPorNumero(numeroSala));
    }

    // CORRECCIÓN [89]: @PostMapping para creación de recurso con @Valid | MOTIVO:
    // Estándar REST (POST para crear) y validación de entrada | ANTES: @PutMapping
    // public Sala crear(@RequestBody Sala sala) { return salaRepository.save(sala);
    // }
    @PostMapping
    public ResponseEntity<Sala> crear(@Valid @RequestBody Sala sala) {
        Sala creada = salaService.crear(sala);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}
