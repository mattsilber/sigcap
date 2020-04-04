package com.guardanis.sigcap.androidx

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.guardanis.sigcap.dialog.SignatureDialogBuilder
import com.guardanis.sigcap.dialog.SignatureDialogFragment.DEFAULT_DIALOG_TAG
import com.guardanis.sigcap.SignatureEventListener
import com.guardanis.sigcap.SignatureInputView.*

/**
 * Create an [AppCompatSignatureDialogFragment] and show it.
 *
 * @param fragmentManager The [FragmentManager] provided by [android.app.Activity]
 *      or [androidx.fragment.app.Fragment].
 * @param tag A string to find the created [SignatureDialogFragment] or null
 *      to default to [SignatureDialogFragment.DEFAULT_DIALOG_TAG]
 * @param eventListener The [SignatureEventListener] instance or null if
 *      you wish to manually supply it later
 */
public fun SignatureDialogBuilder.showAppCompatDialogFragment(
        fragmentManager: FragmentManager,
        tag: String = DEFAULT_DIALOG_TAG,
        eventListener: SignatureEventListener? = null) {

    val arguments = Bundle()
    arguments.putParcelable(KEY__SIGNATURE_REQUEST, request)
    arguments.putParcelable(KEY__SIGNATURE_RENDERER, renderer)
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