package pl.pilionerzy.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.IllegalPrefixException;
import pl.pilionerzy.exception.NoSuchGameException;

@ControllerAdvice
public class GameExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccessException(DataAccessException exception, WebRequest request) {
        return handleExceptionInternal(exception, "Database error occurred", new HttpHeaders(),
                HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(GameException.class)
    protected ResponseEntity<Object> handleGameException(GameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IllegalPrefixException.class)
    protected ResponseEntity<Object> handleIllegalPrefixException(IllegalPrefixException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(NoSuchGameException.class)
    protected ResponseEntity<Object> handleNoSuchGameException(NoSuchGameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

}
