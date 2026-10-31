package com.cine.services;

import com.cine.exceptions.ResourceNotFoundException;
import com.cine.models.Cliente;
import com.cine.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// CORRECCIÓN [73]: ClienteService con inyección por constructor y lógica de registro correcta sin DTOs | MOTIVO: Modelo de referencia Cliente e Inconsistencias conocidas (idCliente PK String natural, numeroCliente único, activo, fechaRegistro) | ANTES: Inyección @Autowired por campo, listar() retornaba List.of(), registrar() forzaba cliente.setRegistrado(false)
@Service
public class ClienteService {

    // CORRECCIÓN [74]: Inyección por constructor en lugar de @Autowired por campo | MOTIVO: Buenas prácticas de diseño de software y testeabilidad | ANTES: @Autowired private ClienteRepository clienteRepository;
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // CORRECCIÓN [75]: listar() retorna los clientes de la base de datos | MOTIVO: Corrección de bug donde listar() retornaba lista vacía | ANTES: public List<Cliente> listar() { return List.of(); }
    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente obtenerPorId(String idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente));
    }

    // CORRECCIÓN [76]: registrar() con idCliente natural, validación de unicidad de ID y email | MOTIVO: Restricción semántica 3 y modelo de referencia | ANTES: cliente.setRegistrado(false); return clienteRepository.save(cliente);
    @Transactional
    public Cliente registrar(Cliente cliente) {
        if (clienteRepository.existsById(cliente.getIdCliente())) {
            throw new IllegalArgumentException("Ya existe un cliente con ID: " + cliente.getIdCliente());
        }
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con email: " + cliente.getEmail());
        }

        if (cliente.getFechaRegistro() == null) {
            cliente.setFechaRegistro(LocalDateTime.now());
        }
        if (cliente.getActivo() == null) {
            cliente.setActivo(true);
        }
        if (cliente.getPuntos() == null) {
            cliente.setPuntos(0);
        }

        return clienteRepository.save(cliente);
    }

    // AGREGADO [77]: consultarPuntos() para obtener los puntos acumulados de un cliente | MOTIVO: Completar funcionalidad pendiente en TODO de la versión anterior
    @Transactional(readOnly = true)
    public Integer consultarPuntos(String idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente));
        return cliente.getPuntos();
    }
}
