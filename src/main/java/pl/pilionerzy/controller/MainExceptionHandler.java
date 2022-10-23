package pl.pilionerzy.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class MainExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DATABASE_ERROR = "Database error occurred";

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception, WebRequest request) {
        return handleExceptionInternal(exception, prepareMessage(exception), new HttpHeaders(), BAD_REQUEST, request);
    }

    private String prepareMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(joining(", ", "Constraint violations: ", ""));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(exception, prepareMessage(exception), headers, BAD_REQUEST, request);
    }

    private String prepareMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(joining(", ", "Validation errors: ", ""));
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccessException(DataAccessException exception, WebRequest request) {
        logger.error(DATABASE_ERROR, exception);
        return handleExceptionInternal(exception, DATABASE_ERROR, new HttpHeaders(), SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(GameException.class)
    protected ResponseEntity<Object> handleGameException(GameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(LifelineException.class)
    protected ResponseEntity<Object> handleLifelineException(LifelineException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), FORBIDDEN, request);
    }

    @ExceptionHandler(NoSuchGameException.class)
    protected ResponseEntity<Object> handleNoSuchGameException(NoSuchGameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(), NOT_FOUND, request);
    }
}
