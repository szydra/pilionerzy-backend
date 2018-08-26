package pl.pilionerzy.exception;

public class IllegalPrefixException extends RuntimeException {

    public IllegalPrefixException(String prefix) {
        super("Illegal prefix detected: " + prefix);
    }

}
