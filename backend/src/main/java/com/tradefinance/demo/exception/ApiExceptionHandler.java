package com.tradefinance.demo.exception;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final int ORA_RESOURCE_BUSY_TIMEOUT = 30006;
    private static final int ORA_RESOURCE_BUSY_NOWAIT = 54;
    private static final int ORA_USER_ERROR_MIN = 20000;
    private static final int ORA_USER_ERROR_MAX = 20999;

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidArgument(IllegalArgumentException error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, error.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    ProblemDetail handleMalformedRequest(Exception error) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Provide valid numeric input.");
    }

    @ExceptionHandler(DataAccessException.class)
    ProblemDetail maskDatabaseError(DataAccessException error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sql) {
                if (sql.getErrorCode() == ORA_RESOURCE_BUSY_TIMEOUT || sql.getErrorCode() == ORA_RESOURCE_BUSY_NOWAIT) {
                    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "Another calculation is running. Try again.");
                }
                if (sql.getErrorCode() >= ORA_USER_ERROR_MIN && sql.getErrorCode() <= ORA_USER_ERROR_MAX) {
                    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, applicationErrorMessage(sql));
                }
            }
        }
        log.error("Database operation failed", error);
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Database operation unavailable. Please try again later.");
    }

    /** Oracle prefixes RAISE_APPLICATION_ERROR messages with "ORA-nnnnn: " and appends ORA-06512 call-stack lines. */
    private static String applicationErrorMessage(SQLException sql) {
        String message = sql.getMessage();
        if (message == null) {
            return "The request could not be completed.";
        }
        return message.split("\n", 2)[0].replaceFirst("^ORA-\\d+:\\s*", "").trim();
    }
}
