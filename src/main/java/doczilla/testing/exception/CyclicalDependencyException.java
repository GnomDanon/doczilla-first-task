package doczilla.testing.exception;

/**
 * Ошибка, выбрасываемая при обнаружении циклической зависимости.
 */
public class CyclicalDependencyException extends Exception {
    public CyclicalDependencyException(String message) {
        super(message);
    }
}
