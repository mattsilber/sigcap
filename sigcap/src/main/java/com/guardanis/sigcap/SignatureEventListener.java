package com.guardanis.sigcap;

public interface SignatureEventListener {
    public void onSignatureEntered(SignatureResponse response);
    public void onSignatureInputCanceled();
    public void onSignatureInputError(Throwable e);
}