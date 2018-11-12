package pl.pilionerzy.exception;

/**
 * This exception is thrown when a request for a non-existing game is performed.
 */
public class NoSuchGameException extends RuntimeException {

    public NoSuchGameException(Long gameId) {
        super(String.format("Game with id %s not found", gameId));
    }

}
