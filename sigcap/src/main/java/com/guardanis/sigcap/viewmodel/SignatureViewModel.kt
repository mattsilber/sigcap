package com.guardanis.sigcap.viewmodel

import android.graphics.Path
import androidx.lifecycle.ViewModel
import java.util.*

class SignatureViewModel : ViewModel() {
    var signaturePaths: List<List<Path>> = ArrayList()
}