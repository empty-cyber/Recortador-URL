package com.acortador.exception;

public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String code) {
        super("No existe un enlace con el código: " + code);
    }
}
