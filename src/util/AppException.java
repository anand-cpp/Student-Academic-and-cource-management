package util;

/**
 * Custom application exception.
 *
 * <p>Thrown by the service and DAO layers to report business-rule violations,
 * validation failures, duplicate-key errors, and database problems in a way the
 * user interface can present as a friendly message. It is an unchecked exception
 * so DAO methods do not need to declare it everywhere, but callers are expected
 * to catch it and show a JOptionPane rather than exposing a raw stack trace.
 *
 * <p>This class demonstrates encapsulation (private fields with getters) and
 * clear, meaningful error reporting instead of silently ignoring failures.
 */
public class AppException extends RuntimeException {

    private final String errorCode;

    public AppException(String message) {
        super(message);
        this.errorCode = "ERR";
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "ERR";
    }

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
