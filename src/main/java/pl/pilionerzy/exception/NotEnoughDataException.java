package pl.pilionerzy.exception;

import org.springframework.dao.DataAccessException;

/**
 * This exception is thrown in the case where the amount of data in database
 * is not sufficient to continue a game, e.g., there are very few questions.
 */
public class NotEnoughDataException extends DataAccessException {

    public NotEnoughDataException(String message) {
        super(message);
    }

}
