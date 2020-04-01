package com.guardanis.sigcap.androidx

import androidx.fragment.app.FragmentManager
import com.guardanis.sigcap.SignatureDialogBuilder
import com.guardanis.sigcap.SignatureDialogFragment.DEFAULT_DIALOG_TAG
import com.guardanis.sigcap.SignatureEventListener

public fun SignatureDialogBuilder.showAppCompatDialogFragment(
        fragmentManager: FragmentManager,
        tag: String = DEFAULT_DIALOG_TAG,
        eventListener: SignatureEventListener? = null) {

    AppCompatSignatureDialogFragment()
            .setSignatureRequest(request)
            .setSignatureRenderer(signatureRenderer)
            .setSignaturePathManager(pathManager)
            .setSignatureEventListener(eventListener)
            .show(fragmentManager, tag);
}

public fun FragmentManager.findAppCompatSignatureDialogFragment(
        tag: String = DEFAULT_DIALOG_TAG): AppCompatSignatureDialogFragment? {

    return this.findFragmentByTag(tag) as? AppCompatSignatureDialogFragment
}