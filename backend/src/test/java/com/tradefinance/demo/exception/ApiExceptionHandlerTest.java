package com.tradefinance.demo.exception;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.UncategorizedSQLException;
import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @ParameterizedTest
    @ValueSource(ints = {20000, 20001, 20999})
    void applicationErrorRangeMapsTo422(int errorCode) {
        var error = new UncategorizedSQLException("call", "sql",
            new SQLException("ORA-" + errorCode + ": business rule violated", "99999", errorCode));

        var result = handler.maskDatabaseError(error);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    void unrecognizedErrorCodeMapsTo503() {
        var error = new UncategorizedSQLException("call", "sql",
            new SQLException("connection lost", "08006", 17002));

        var result = handler.maskDatabaseError(error);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
    }
}
