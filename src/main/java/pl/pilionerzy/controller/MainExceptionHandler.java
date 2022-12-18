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
        return doHandleWithMessage(exception, request, BAD_REQUEST, prepareMessage(exception));
    }

    private String prepareMessage(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(joining(", ", "Constraint violations: ", ""));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return doHandleWithMessage(exception, request, BAD_REQUEST, prepareMessage(exception));
    }

    private String prepareMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(joining(", ", "Validation errors: ", ""));
    }

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccessException(DataAccessException exception, WebRequest request) {
        logger.error(DATABASE_ERROR, exception);
        return doHandleWithMessage(exception, request, SERVICE_UNAVAILABLE, DATABASE_ERROR);
    }

    @ExceptionHandler(GameException.class)
    protected ResponseEntity<Object> handleGameException(GameException exception, WebRequest request) {
        return doHandle(exception, request, BAD_REQUEST);
    }

    @ExceptionHandler(LifelineException.class)
    protected ResponseEntity<Object> handleLifelineException(LifelineException exception, WebRequest request) {
        return doHandle(exception, request, FORBIDDEN);
    }

    @ExceptionHandler(NoSuchGameException.class)
    protected ResponseEntity<Object> handleNoSuchGameException(NoSuchGameException exception, WebRequest request) {
        return doHandle(exception, request, NOT_FOUND);
    }

    private ResponseEntity<Object> doHandle(Exception exception, WebRequest request, HttpStatus status) {
        return doHandleWithMessage(exception, request, status, exception.getMessage());
    }

    private ResponseEntity<Object> doHandleWithMessage(Exception exception, WebRequest request, HttpStatus status, String message) {
        return handleExceptionInternal(exception, message, new HttpHeaders(), status, request);
    }
}
