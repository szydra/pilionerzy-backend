package pl.pilionerzy.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.pilionerzy.exception.GameException;
import pl.pilionerzy.exception.LifelineException;
import pl.pilionerzy.exception.NoSuchGameException;

@ControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<Object> handleDataAccessException(DataAccessException exception, WebRequest request) {
        return handleExceptionInternal(exception, "Database error occurred", new HttpHeaders(),
                HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(GameException.class)
    protected ResponseEntity<Object> handleGameException(GameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.BAD_REQUEST, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(LifelineException.class)
    protected ResponseEntity<Object> handleLifelineException(LifelineException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.FORBIDDEN, request);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoSuchGameException.class)
    protected ResponseEntity<Object> handleNoSuchGameException(NoSuchGameException exception, WebRequest request) {
        return handleExceptionInternal(exception, exception.getMessage(), new HttpHeaders(),
                HttpStatus.NOT_FOUND, request);
    }

}
