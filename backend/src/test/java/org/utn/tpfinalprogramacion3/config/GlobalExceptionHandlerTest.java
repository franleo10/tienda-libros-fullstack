package org.utn.tpfinalprogramacion3.config;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    @Test
    void dataIntegrityErrorsDoNotExposeDatabaseDetails() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        String databaseDetails = "Duplicate entry 'adminGian' for key 'usuarios.username'";

        var response = handler.handleDataIntegrityViolationException(
                new DataIntegrityViolationException(databaseDetails)
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "No se pudo completar la operación porque los datos entran en conflicto con un registro existente.",
                response.getBody()
        );
        assertFalse(response.getBody().contains(databaseDetails));
    }
}
