package com.cine.services;

import com.cine.exceptions.BusinessRuleException;
import com.cine.exceptions.ResourceNotFoundException;
import com.cine.models.Boleto;
import com.cine.models.Cliente;
import com.cine.models.Funcion;
import com.cine.repositories.BoletoRepository;
import com.cine.repositories.ClienteRepository;
import com.cine.repositories.FuncionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

// CORRECCIÓN [83]: BoletoService con validación de capacidad de sala, asiento único, UUID en folio, cálculo de puntos directamente sobre modelo Boleto | MOTIVO: Restricciones semánticas 5 y 6, Naturaleza de identificadores (folio UUID) y cálculo correcto de puntos sin DTOs | ANTES: Inyección por campo @Autowired, sin validación de capacidad, sin validación de asiento ocupado, folio hardcodeado "BOLETO", error de compilación por tipos incompatibles (double a Integer)
@Service
public class BoletoService {

    // CORRECCIÓN [84]: Inyección por constructor en lugar de @Autowired por campo | MOTIVO: Buenas prácticas de diseño de software y testeabilidad | ANTES: @Autowired en campos boletoRepository, funcionRepository y clienteRepository
    private final BoletoRepository boletoRepository;
    private final FuncionRepository funcionRepository;
    private final ClienteRepository clienteRepository;

    public BoletoService(BoletoRepository boletoRepository,
                         FuncionRepository funcionRepository,
                         ClienteRepository clienteRepository) {
        this.boletoRepository = boletoRepository;
        this.funcionRepository = funcionRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Boleto> porFuncion(String funcionId) {
        return boletoRepository.findByFuncionIdFuncion(funcionId);
    }

    // CORRECCIÓN [85]: comprar() implementa Regla de negocio 5 (capacidad máxima) y Regla de negocio 6 (asiento único) operando con entidades | MOTIVO: Restricción semántica 5: no vender más boletos que la capacidad de la sala; Restricción semántica 6: no permitir asiento duplicado en la misma función; folio UUID | ANTES: Error de tipos al compilar, folio "BOLETO", sin validación de capacidad ni unicidad de asiento
    @Transactional
    public Boleto comprar(String idFuncion, String idCliente, Integer filaAsiento, Integer numeroAsiento) {
        if (idFuncion == null || idFuncion.isBlank()) {
            throw new IllegalArgumentException("El idFuncion es obligatorio");
        }
        if (idCliente == null || idCliente.isBlank()) {
            throw new IllegalArgumentException("El idCliente es obligatorio");
        }
        if (filaAsiento == null || filaAsiento <= 0) {
            throw new IllegalArgumentException("La filaAsiento debe ser un entero positivo");
        }
        if (numeroAsiento == null || numeroAsiento <= 0) {
            throw new IllegalArgumentException("El numeroAsiento debe ser un entero positivo");
        }

        Funcion funcion = funcionRepository.findById(idFuncion)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con ID: " + idFuncion));

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + idCliente));

        // Regla de Negocio 5: Los boletos de una función NO pueden exceder la capacidad de la sala
        long boletosVendidos = boletoRepository.countByFuncionIdFuncion(idFuncion);
        int capacidadSala = funcion.getSala().getCapacidad();
        if (boletosVendidos >= capacidadSala) {
            throw new BusinessRuleException("No se pueden vender más boletos: la sala ha alcanzado su capacidad máxima de " + capacidadSala + " asientos");
        }

        // Regla de Negocio 6: No puede haber dos boletos para el mismo asiento (filaAsiento + numeroAsiento) en la misma función
        boolean asientoOcupado = boletoRepository.existsByFuncionIdFuncionAndFilaAsientoAndNumeroAsiento(
                idFuncion,
                filaAsiento,
                numeroAsiento
        );
        if (asientoOcupado) {
            throw new BusinessRuleException(String.format("El asiento fila %d, número %d ya se encuentra reservado para esta función",
                    filaAsiento, numeroAsiento));
        }

        // Cálculo de puntos generados (10% del precio de la función en enteros)
        BigDecimal puntosCalculados = funcion.getPrecio().divide(BigDecimal.valueOf(10), 0, RoundingMode.HALF_UP);
        int puntos = puntosCalculados.intValue();

        // Si el cliente está activo, acumula puntos
        if (Boolean.TRUE.equals(cliente.getActivo())) {
            cliente.setPuntos(cliente.getPuntos() + puntos);
            clienteRepository.save(cliente);
        }

        String folio = UUID.randomUUID().toString();
        Boleto boleto = new Boleto(
                folio,
                filaAsiento,
                numeroAsiento,
                funcion,
                cliente,
                puntos
        );

        return boletoRepository.save(boleto);
    }
}
