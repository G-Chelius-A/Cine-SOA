package com.cine.services;

import com.cine.exceptions.BusinessRuleException;
import com.cine.models.Boleto;
import com.cine.models.Cliente;
import com.cine.models.Funcion;
import com.cine.models.Sala;
import com.cine.repositories.BoletoRepository;
import com.cine.repositories.ClienteRepository;
import com.cine.repositories.FuncionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// AGREGADO [109]: Pruebas unitarias para reglas de negocio de BoletoService con dynamic proxies limpios sin DTOs | MOTIVO: Tarea 5: Pruebas unitarias para las dos reglas de negocio (capacidad máxima de sala y asiento único)
class BoletoServiceTest {

    private BoletoService boletoService;
    private BoletoRepository boletoRepository;
    private FuncionRepository funcionRepository;
    private ClienteRepository clienteRepository;

    private Funcion funcion;
    private Sala sala;
    private Cliente cliente;

    private long boletosCountMock;
    private boolean asientoOcupadoMock;
    private Boleto boletoGuardadoMock;

    @BeforeEach
    void setUp() {
        boletosCountMock = 0L;
        asientoOcupadoMock = false;
        boletoGuardadoMock = null;

        sala = new Sala(1L, 2); // Capacidad de 2 personas
        funcion = new Funcion("func-uuid-1", null, sala, LocalDateTime.now(), new BigDecimal("85.00"));
        cliente = new Cliente("CLI-001", "Juan Perez", "juan@cine.com", true, LocalDateTime.now(), 10);

        // Proxy dinámico para BoletoRepository
        boletoRepository = (BoletoRepository) Proxy.newProxyInstance(
                BoletoRepository.class.getClassLoader(),
                new Class<?>[]{BoletoRepository.class},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    if ("countByFuncionIdFuncion".equals(methodName)) {
                        return boletosCountMock;
                    }
                    if ("existsByFuncionIdFuncionAndFilaAsientoAndNumeroAsiento".equals(methodName)) {
                        return asientoOcupadoMock;
                    }
                    if ("save".equals(methodName)) {
                        boletoGuardadoMock = (Boleto) args[0];
                        return args[0];
                    }
                    return null;
                }
        );

        // Proxy dinámico para FuncionRepository
        funcionRepository = (FuncionRepository) Proxy.newProxyInstance(
                FuncionRepository.class.getClassLoader(),
                new Class<?>[]{FuncionRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        String id = (String) args[0];
                        if ("func-uuid-1".equals(id)) {
                            return Optional.of(funcion);
                        }
                        return Optional.empty();
                    }
                    return null;
                }
        );

        // Proxy dinámico para ClienteRepository
        clienteRepository = (ClienteRepository) Proxy.newProxyInstance(
                ClienteRepository.class.getClassLoader(),
                new Class<?>[]{ClienteRepository.class},
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName())) {
                        String id = (String) args[0];
                        if ("CLI-001".equals(id)) {
                            return Optional.of(cliente);
                        }
                        return Optional.empty();
                    }
                    if ("save".equals(method.getName())) {
                        return args[0];
                    }
                    return null;
                }
        );

        boletoService = new BoletoService(boletoRepository, funcionRepository, clienteRepository);
    }

    @Test
    @DisplayName("Regla de negocio 5: Debe lanzar BusinessRuleException cuando se excede la capacidad de la sala")
    void testNoVenderMasBoletosQueCapacidadSala() {
        // La sala tiene capacidad de 2, y ya hay 2 vendidos
        boletosCountMock = 2L;

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            boletoService.comprar("func-uuid-1", "CLI-001", 1, 1);
        });

        assertTrue(exception.getMessage().contains("capacidad máxima"));
        assertNull(boletoGuardadoMock);
    }

    @Test
    @DisplayName("Regla de negocio 6: Debe lanzar BusinessRuleException cuando el asiento ya está ocupado en la misma función")
    void testNoPermitirAsientoDuplicadoEnMismaFuncion() {
        // Capacidad no excedida (1 vendido de 2)
        boletosCountMock = 1L;
        // Asiento marcado como ya ocupado
        asientoOcupadoMock = true;

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            boletoService.comprar("func-uuid-1", "CLI-001", 1, 3);
        });

        assertTrue(exception.getMessage().contains("ya se encuentra reservado"));
        assertNull(boletoGuardadoMock);
    }

    @Test
    @DisplayName("Compra exitosa: Asigna UUID, folio, puntos y guarda el boleto")
    void testCompraExitosa() {
        boletosCountMock = 0L;
        asientoOcupadoMock = false;

        Boleto response = boletoService.comprar("func-uuid-1", "CLI-001", 1, 1);

        assertNotNull(response);
        assertNotNull(response.getFolio());
        assertEquals("func-uuid-1", response.getFuncion().getIdFuncion());
        assertEquals("CLI-001", response.getCliente().getIdCliente());
        assertEquals(1, response.getFilaAsiento());
        assertEquals(1, response.getNumeroAsiento());
        // 85.00 / 10 = 8.5 redondeado a 9 puntos
        assertEquals(9, response.getPuntosGenerados());
        assertEquals(19, cliente.getPuntos()); // 10 previos + 9

        assertNotNull(boletoGuardadoMock);
        assertEquals(response.getFolio(), boletoGuardadoMock.getFolio());
    }
}
