package com.guardanis.sigcap.exceptions;

public class BadSignaturePathException extends RuntimeException {

    public BadSignaturePathException(Exception value) {
        super(value);
    }

    public BadSignaturePathException(String value) {
        super(value);
    }
}
