package com.cine.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// AGREGADO [110]: Pruebas unitarias para validaciones Bean Validation de Email y Clasificación Enum en modelos | MOTIVO: Tarea 5: Pruebas para validaciones de enum y email (Restricción semántica 1 y 3) directamente en entidades sin DTOs
class ValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Restricción semántica 3: Formato de email válido e inválido en entidad Cliente")
    void testEmailValidation() {
        // Email válido
        Cliente clienteValido = new Cliente("CLI-1", "Carlos", "carlos@cine.com");
        Set<ConstraintViolation<Cliente>> violationsValido = validator.validate(clienteValido);
        assertTrue(violationsValido.isEmpty(), "Un email con formato correcto no debe tener violaciones");

        // Email inválido sin arroba ni dominio
        Cliente clienteInvalido = new Cliente("CLI-2", "Carlos", "email-invalido");
        Set<ConstraintViolation<Cliente>> violationsInvalido = validator.validate(clienteInvalido);
        assertFalse(violationsInvalido.isEmpty(), "Un email sin formato correcto debe tener violaciones");
        assertTrue(violationsInvalido.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));

        // Email inválido sin extensión completa
        Cliente clienteInvalido2 = new Cliente("CLI-3", "Carlos", "carlos@");
        Set<ConstraintViolation<Cliente>> violationsInvalido2 = validator.validate(clienteInvalido2);
        assertFalse(violationsInvalido2.isEmpty(), "Un email sin dominio debe fallar");
    }

    @Test
    @DisplayName("Restricción semántica 1: Valores válidos del Enum Clasificación (A, B, B15, C)")
    void testClasificacionEnumValues() {
        assertEquals(4, Clasificacion.values().length);
        assertNotNull(Clasificacion.valueOf("A"));
        assertNotNull(Clasificacion.valueOf("B"));
        assertNotNull(Clasificacion.valueOf("B15"));
        assertNotNull(Clasificacion.valueOf("C"));

        assertThrows(IllegalArgumentException.class, () -> {
            Clasificacion.valueOf("ATP"); // Clasificación no admitida
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Clasificacion.valueOf("D"); // Clasificación no admitida
        });
    }

    @Test
    @DisplayName("Bean Validation en Pelicula: clasificacion obligatoria y no nula")
    void testPeliculaValidation() {
        Pelicula pValida = new Pelicula("PEL-1", "Matrix", "Sinopsis", Clasificacion.B, 136, "Sci-Fi");
        Set<ConstraintViolation<Pelicula>> violationsValida = validator.validate(pValida);
        assertTrue(violationsValida.isEmpty());

        Pelicula pInvalida = new Pelicula("PEL-2", "Matrix", "Sinopsis", null, 136, "Sci-Fi");
        Set<ConstraintViolation<Pelicula>> violationsInvalida = validator.validate(pInvalida);
        assertFalse(violationsInvalida.isEmpty());
        assertTrue(violationsInvalida.stream().anyMatch(v -> v.getPropertyPath().toString().equals("clasificacion")));
    }
}

