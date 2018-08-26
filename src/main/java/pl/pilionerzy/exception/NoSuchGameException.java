package pl.pilionerzy.exception;

public class NoSuchGameException extends RuntimeException {

    public NoSuchGameException(Long gameId) {
        super(String.format("Game with id %s not found", gameId));
    }

}
