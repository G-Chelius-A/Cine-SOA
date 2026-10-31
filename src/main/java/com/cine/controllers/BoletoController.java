package com.cine.controllers;

import com.cine.models.Boleto;
import com.cine.services.BoletoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CORRECCIÓN [105]: BoletoController con inyección por constructor, @PostMapping para comprar boletos y ruta REST correcta sin DTOs | MOTIVO: Tarea 3: Corregir @GetMapping usado para mutaciones/comprar boletos, usar POST y parámetros de compra | ANTES: @Autowired por campo, comprar era @GetMapping con @RequestParam
@RestController
@RequestMapping("/boletos")
public class BoletoController {

    // CORRECCIÓN [106]: Inyección por constructor de BoletoService | MOTIVO: Buenas prácticas de arquitectura | ANTES: @Autowired private BoletoService boletoService;
    private final BoletoService boletoService;

    public BoletoController(BoletoService boletoService) {
        this.boletoService = boletoService;
    }

    // CORRECCIÓN [107]: Listar boletos por función recibiendo String funcionId (UUID) | MOTIVO: Naturaleza de identificadores: idFuncion es UUID String | ANTES: public List<Boleto> porFuncion(@PathVariable Long funcionId)
    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<Boleto>> porFuncion(@PathVariable String funcionId) {
        return ResponseEntity.ok(boletoService.porFuncion(funcionId));
    }

    // CORRECCIÓN [108]: @PostMapping para comprar boletos con parámetros de compra | MOTIVO: Corrección de mala práctica REST de usar @GetMapping para operaciones de escritura/compra | ANTES: @GetMapping public Boleto comprar(@RequestParam Long funcionId, @RequestParam Long clienteId, @RequestParam String fila, @RequestParam int numero)
    @PostMapping
    public ResponseEntity<Boleto> comprar(
            @RequestParam String idFuncion,
            @RequestParam String idCliente,
            @RequestParam Integer filaAsiento,
            @RequestParam Integer numeroAsiento) {
        Boleto boleto = boletoService.comprar(idFuncion, idCliente, filaAsiento, numeroAsiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(boleto);
    }
}
