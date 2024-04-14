package br.com.due.meudin.exception;

public class JWTCreationError extends RuntimeException {

    public JWTCreationError(String message, Throwable cause) {
        super(message, cause);
    }
}
