package com.acortador.exception;

public class AliasAlreadyTakenException extends RuntimeException {

    public AliasAlreadyTakenException(String alias) {
        super("El alias '" + alias + "' ya está en uso. Elige otro.");
    }
}
