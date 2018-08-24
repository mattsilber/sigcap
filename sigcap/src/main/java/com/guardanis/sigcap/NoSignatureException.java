package com.guardanis.sigcap;

public class NoSignatureException extends RuntimeException {

    public NoSignatureException(String value){
        super(value);
    }
}
