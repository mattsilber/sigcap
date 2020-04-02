package com.guardanis.sigcap.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.guardanis.sigcap.*
import com.guardanis.sigcap.androidx.findAppCompatSignatureDialogFragment
import com.guardanis.sigcap.androidx.showAppCompatDialogFragment
import com.guardanis.sigcap.dialog.SignatureDialogBuilder
import com.guardanis.sigcap.dialog.SignatureDialogFragment
import com.guardanis.sigcap.exceptions.NoSignatureException

class MainActivity: AppCompatActivity(), SignatureEventListener {

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        setContentView(R.layout.activity_main)
    }

    override fun onPause() {
        FileCache.clear(this)

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        // If our DialogFragment exists, we must reset the SignatureEventListener
        supportFragmentManager.findAppCompatSignatureDialogFragment(SignatureDialogFragment.DEFAULT_DIALOG_TAG)
                ?.setSignatureEventListener(this)

        // If our DialogFragment exists, we must reset the SignatureEventListener
        fragmentManager.findFragmentByTag(SignatureDialogFragment.DEFAULT_DIALOG_TAG)
                ?.let({ it as? SignatureDialogFragment })
                ?.setSignatureEventListener(this)
    }

    fun startClicked(view: View?) {
        SignatureDialogBuilder()
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                )
                .showDialogFragment(fragmentManager, SignatureDialogFragment.DEFAULT_DIALOG_TAG, this)
    }

    fun startAppCompatClicked(view: View?) {
        SignatureDialogBuilder()
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                )
                .showAppCompatDialogFragment(supportFragmentManager, eventListener = this)
    }

    fun startStatelessClicked(view: View?) {
        SignatureDialogBuilder()
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                )
                .showStatelessAlertDialog(this, this)
    }

    override fun onSignatureEntered(response: SignatureResponse) {
        findViewById<AppCompatImageView>(R.id.main__image)
                .setImageBitmap(response.result)

        val savedFile = response.saveToFileCache(this)
                .get()
    }

    override fun onSignatureInputCanceled() {
        Toast.makeText(this, "Signature input canceled", Toast.LENGTH_SHORT)
                .show()
    }

    override fun onSignatureInputError(e: Throwable) {
        if (e is NoSignatureException) {
            // They clicked confirm without entering anything
            // doSomethingOnNoSignatureEntered()
        }

        Toast.makeText(this, "Signature error", Toast.LENGTH_SHORT)
                .show()
    }
}