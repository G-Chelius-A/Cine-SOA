package com.cine.controllers;

import com.cine.models.Funcion;
import com.cine.services.FuncionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// CORRECCIÓN [100]: FuncionController con inyección por constructor, representación anidada natural de modelo Funcion y parámetros REST claros | MOTIVO: Tarea 4 (representación anidada), Naturaleza de identificadores: Funcion.idFuncion UUID String y eliminación de DTOs | ANTES: @Autowired por campo, obtener(@RequestParam Long id) con mismatch en ruta /{id}, programar recibía params y entidad cruda
@RestController
@RequestMapping("/funciones")
public class FuncionController {

    // CORRECCIÓN [101]: Inyección por constructor de FuncionService | MOTIVO: Buenas prácticas de diseño de software | ANTES: @Autowired private FuncionService funcionService;
    private final FuncionService funcionService;

    public FuncionController(FuncionService funcionService) {
        this.funcionService = funcionService;
    }

    // CORRECCIÓN [102]: Listar funciones con su Sala y Pelicula anidadas | MOTIVO: Tarea 4: Representación JSON anidada de Función con su Película y su Sala | ANTES: public List<Funcion> listar() { return List.of(); }
    @GetMapping
    public ResponseEntity<List<Funcion>> listar() {
        return ResponseEntity.ok(funcionService.listar());
    }

    // CORRECCIÓN [103]: @PathVariable String idFuncion en lugar de @RequestParam Long id | MOTIVO: Naturaleza de identificadores: Funcion.idFuncion es UUID como String | ANTES: @GetMapping("/{id}") public Funcion obtener(@RequestParam Long id)
    @GetMapping("/{idFuncion}")
    public ResponseEntity<Funcion> obtener(@PathVariable String idFuncion) {
        return ResponseEntity.ok(funcionService.obtener(idFuncion));
    }

    // CORRECCIÓN [104]: @PostMapping para programar función con idPelicula, idSala, fechaHora y precio | MOTIVO: Modelo de referencia: idSala FK a Sala.numeroSala, idPelicula FK a Pelicula.idPelicula, precio BigDecimal | ANTES: @PostMapping public Funcion programar(@RequestParam Long peliculaId, @RequestParam Long salaId, @RequestBody Funcion funcion)
    @PostMapping
    public ResponseEntity<Funcion> programar(
            @RequestParam String idPelicula,
            @RequestParam Long idSala,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam BigDecimal precio) {
        Funcion creada = funcionService.programar(idPelicula, idSala, fechaHora, precio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}
