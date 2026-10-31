package com.cine.controllers;

import com.cine.models.Cliente;
import com.cine.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CORRECCIÓN [95]: ClienteController con inyección por constructor, validación en entidad Cliente y método POST | MOTIVO: Tarea 3: Corregir @PutMapping usado para creación, retornar lista real y usar validación @Email en Cliente sin DTOs | ANTES: @Autowired por campo, listar retornaba List.of(), @PutMapping para registrar
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    // CORRECCIÓN [96]: Inyección por constructor de ClienteService | MOTIVO: Buenas
    // prácticas de arquitectura | ANTES: @Autowired private ClienteService
    // clienteService;
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // CORRECCIÓN [97]: Listar clientes delegando al servicio | MOTIVO: Corrección
    // de bug donde listar() retornaba lista vacía | ANTES: public List<Cliente>
    // listar() { return List.of(); }
    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable String idCliente) {
        return ResponseEntity.ok(clienteService.obtenerPorId(idCliente));
    }

    // AGREGADO [98]: Endpoint para consultar puntos acumulados de un cliente |
    // MOTIVO: Modelo de referencia: consultar puntos
    @GetMapping("/{idCliente}/puntos")
    public ResponseEntity<Integer> consultarPuntos(@PathVariable String idCliente) {
        return ResponseEntity.ok(clienteService.consultarPuntos(idCliente));
    }

    // CORRECCIÓN [99]: @PostMapping para registro de cliente con @Valid Cliente | MOTIVO: Estándar REST (POST para crear recurso) y validación de @Email | ANTES: @PutMapping public Cliente registrar(@RequestBody Cliente cliente)
    @PostMapping
    public ResponseEntity<Cliente> registrar(@Valid @RequestBody Cliente cliente) {
        Cliente registrado = clienteService.registrar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }
}
