package org.example;

public class PublicKeyRequestException extends RuntimeException {

    PublicKeyRequestException(final Exception exception) {
        super(exception);
    }
}