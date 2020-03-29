package com.guardanis.sigcap

import java.io.File

interface SignatureEventListener {
    fun onSignatureEntered(savedFile: File)
    fun onSignatureInputCanceled()
    fun onSignatureInputError(e: Throwable)
}