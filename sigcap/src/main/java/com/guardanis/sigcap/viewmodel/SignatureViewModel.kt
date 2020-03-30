package com.guardanis.sigcap.viewmodel

import android.graphics.Path
import androidx.lifecycle.ViewModel
import com.guardanis.sigcap.SignatureRenderer
import com.guardanis.sigcap.SignatureRequest
import java.util.*

class SignatureViewModel : ViewModel() {
    var signaturePaths: List<List<Path>> = ArrayList()
    var request: SignatureRequest? = null
    var renderer: SignatureRenderer? = null
}