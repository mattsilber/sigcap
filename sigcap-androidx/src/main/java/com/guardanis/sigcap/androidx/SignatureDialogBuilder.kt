package com.guardanis.sigcap.androidx

import androidx.fragment.app.FragmentManager
import com.guardanis.sigcap.SignatureDialogBuilder

public fun SignatureDialogBuilder.showAppCompat(fragmentManager: FragmentManager, tag: String) {
    AppCompatSignatureDialogFragment()
            .setRequest(request)
            .setSignatureRenderer(signatureRenderer)
            .setPathManager(pathManager)
            .show(fragmentManager, tag);
}