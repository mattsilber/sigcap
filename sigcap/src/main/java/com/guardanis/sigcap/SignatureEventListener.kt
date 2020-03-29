package com.guardanis.sigcap

interface SignatureEventListener {
    fun onSignatureEntered(response: SignatureResponse)
    fun onSignatureInputCanceled()
    fun onSignatureInputError(e: Throwable)
}