package com.guardanis.sigcap;

public class NoSignatureException extends RuntimeException {
    NoSignatureException(String value){
        super(value);
    }
}
