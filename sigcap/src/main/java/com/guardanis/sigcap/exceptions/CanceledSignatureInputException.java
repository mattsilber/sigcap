package com.guardanis.sigcap.exceptions;

public class CanceledSignatureInputException extends RuntimeException {

    public CanceledSignatureInputException() {
        super("Signature input canceled");
    }
}
