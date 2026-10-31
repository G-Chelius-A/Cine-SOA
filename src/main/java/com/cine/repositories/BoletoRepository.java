package com.cine.repositories;

import com.cine.models.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// CORRECCIÓN [50]: BoletoRepository con String como PK (folio UUID) y métodos para validaciones de reglas de negocio | MOTIVO: Naturaleza de identificadores: Boleto.folio es UUID como String y Reglas 5 y 6 | ANTES: JpaRepository<Boleto, Long> con métodos inconsistentes como findByFuncionIdentifier, findByClienteMail
@Repository
public interface BoletoRepository extends JpaRepository<Boleto, String> {

    List<Boleto> findByFuncionIdFuncion(String idFuncion);

    List<Boleto> findByClienteIdCliente(String idCliente);

    // AGREGADO [51]: Conteo de boletos vendidos para una función | MOTIVO: Restricción semántica 5: regla de negocio de no exceder capacidad de la sala
    long countByFuncionIdFuncion(String idFuncion);

    // AGREGADO [52]: Verificación de existencia de asiento en una función | MOTIVO: Restricción semántica 6: regla de negocio de no permitir dos boletos para el mismo asiento en la misma función
    boolean existsByFuncionIdFuncionAndFilaAsientoAndNumeroAsiento(String idFuncion, Integer filaAsiento, Integer numeroAsiento);
}
