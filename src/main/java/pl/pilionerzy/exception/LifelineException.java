package pl.pilionerzy.exception;

/**
 * This exception is thrown in case of an illegal request for a lifeline.
 * It does not prohibit a game to be continued.
 */
public class LifelineException extends RuntimeException {

    public LifelineException(String message) {
        super(message);
    }

}
