package com.guardanis.sigcap.androidx

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.guardanis.sigcap.dialog.SignatureDialogBuilder
import com.guardanis.sigcap.dialog.SignatureDialogFragment.DEFAULT_DIALOG_TAG
import com.guardanis.sigcap.SignatureEventListener
import com.guardanis.sigcap.SignatureInputView.*

public fun SignatureDialogBuilder.showAppCompatDialogFragment(
        fragmentManager: FragmentManager,
        tag: String = DEFAULT_DIALOG_TAG,
        eventListener: SignatureEventListener? = null) {

    val arguments = Bundle()
    arguments.putParcelable(KEY__SIGNATURE_REQUEST, request)
    arguments.putParcelable(KEY__SIGNATURE_RENDERER, signatureRenderer)
    arguments.putParcelable(KEY__SIGNATURE_PATH_MANAGER, pathManager)

    val fragment = AppCompatSignatureDialogFragment()
    fragment.arguments = arguments
    fragment.setSignatureEventListener(eventListener)
    fragment.show(fragmentManager, tag)
}

public fun FragmentManager.findAppCompatSignatureDialogFragment(
        tag: String = DEFAULT_DIALOG_TAG): AppCompatSignatureDialogFragment? {

    return this.findFragmentByTag(tag) as? AppCompatSignatureDialogFragment
}