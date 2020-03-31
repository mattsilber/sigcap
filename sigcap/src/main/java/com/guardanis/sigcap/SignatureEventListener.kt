package com.guardanis.sigcap

/**
 * Interface that handles user interaction with the signature dialog.
 * @see [SignatureDialogBuilder]
 * @see [SignatureDialogFragment]
 *
 * @author Matt Silber
 * @author Yordan P. Dieguez
 */
interface SignatureEventListener {
    fun onSignatureEntered(response: SignatureResponse)
    fun onSignatureInputCanceled()
    fun onSignatureInputError(e: Throwable)
}