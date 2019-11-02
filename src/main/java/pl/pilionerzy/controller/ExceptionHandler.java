package pl.pilionerzy.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DATABASE_ERROR = "Database error occurred";

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception, WebRequest request) {
        return handleExceptionInternal(exception, prepareMessage(exception), new HttpHeaders(), BAD_REQUEST, request);
    }

    private String prepareMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(joining(", ", "Constraint violations: ", ""));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccessException(DataAccessException exception, WebRequest request) {
        logger.error(DATABASE_ERROR, exception);
        return handleExceptionInternal(exception, DATABASE_ERROR, new HttpHeaders(), SERVICE_UNAVAILABLE, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(GameException.class)
    protected ResponseEntity<Object> handleGameException(GameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), BAD_REQUEST, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(LifelineException.class)
    protected ResponseEntity<Object> handleLifelineException(LifelineException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), FORBIDDEN, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchGameException.class)
    protected ResponseEntity<Object> handleNoSuchGameException(NoSuchGameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), NOT_FOUND, request);
    }
}
