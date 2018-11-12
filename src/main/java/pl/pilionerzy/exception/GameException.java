package pl.pilionerzy.exception;

/**
 * This exception is thrown if an existing game cannot be continued for some reason.
 */
public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }

}
