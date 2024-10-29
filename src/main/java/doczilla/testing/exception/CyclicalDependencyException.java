package doczilla.testing.exception;

public class CyclicalDependencyException extends Exception {
    public CyclicalDependencyException(String message) {
        super(message);
    }
}
