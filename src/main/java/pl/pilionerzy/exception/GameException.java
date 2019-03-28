package pl.pilionerzy.exception;

/**
 * This exception is thrown if an existing game cannot be continued for some reason
 * or an illegal request for it was performed.
 */
public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }
}
