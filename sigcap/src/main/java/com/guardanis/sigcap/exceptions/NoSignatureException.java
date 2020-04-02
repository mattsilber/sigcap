package com.guardanis.sigcap.exceptions;

public class NoSignatureException extends RuntimeException {

    public NoSignatureException(String value) {
        super(value);
    }
}
