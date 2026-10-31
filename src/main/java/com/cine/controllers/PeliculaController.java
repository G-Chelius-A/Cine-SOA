package com.cine.controllers;

import com.cine.models.Clasificacion;
import com.cine.models.Pelicula;
import com.cine.services.PeliculaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CORRECCIÓN [90]: PeliculaController con inyección por constructor, validación en entidad Pelicula y rutas REST estándar | MOTIVO: Tarea 2 y 3: Validaciones Bean Validation, corregir @PathVariable vs @RequestParam y respuesta con ResponseEntity sin DTOs | ANTES: Inyección @Autowired por campo, obtener(@RequestParam Long id) con ruta @GetMapping("/{id}"), @RequestBody omitido en POST
@RestController
@RequestMapping("/peliculas")
public class PeliculaController {

    // CORRECCIÓN [91]: Inyección por constructor de PeliculaService | MOTIVO: Buenas prácticas de arquitectura | ANTES: @Autowired private PeliculaService peliculaService;
    private final PeliculaService peliculaService;

    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // CORRECCIÓN [92]: Listado filtrado correctamente por enum Clasificacion si se proporciona | MOTIVO: Corrección de lógica | ANTES: Se forzaba Clasificacion.A en el controlador
    @GetMapping
    public ResponseEntity<List<Pelicula>> listar(@RequestParam(required = false) Clasificacion clasificacion) {
        if (clasificacion != null) {
            return ResponseEntity.ok(peliculaService.porClasificacion(clasificacion));
        }
        return ResponseEntity.ok(peliculaService.listar());
    }

    // CORRECCIÓN [93]: @PathVariable String idPelicula en vez de @RequestParam Long id | MOTIVO: Naturaleza de identificadores: idPelicula clave natural String y ruta REST /{id} | ANTES: @GetMapping("/{id}") public Pelicula obtener(@RequestParam Long id)
    @GetMapping("/{idPelicula}")
    public ResponseEntity<Pelicula> obtener(@PathVariable String idPelicula) {
        return ResponseEntity.ok(peliculaService.obtener(idPelicula));
    }

    // CORRECCIÓN [94]: @PostMapping con @Valid y @RequestBody usando entidad Pelicula | MOTIVO: Bean Validation y recepción de cuerpo JSON | ANTES: @PostMapping public Pelicula crear(Pelicula pelicula) { return peliculaService.crear(pelicula); }
    @PostMapping
    public ResponseEntity<Pelicula> crear(@Valid @RequestBody Pelicula pelicula) {
        Pelicula creada = peliculaService.crear(pelicula);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
}
