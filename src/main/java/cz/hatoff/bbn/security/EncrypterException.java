package cz.hatoff.bbn.security;

public class EncrypterException extends Exception {

    public EncrypterException() {
        super();
    }

    public EncrypterException(String message) {
        super(message);
    }

    public EncrypterException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncrypterException(Throwable cause) {
        super(cause);
    }
}
