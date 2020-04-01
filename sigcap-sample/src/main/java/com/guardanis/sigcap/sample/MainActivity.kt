package com.guardanis.sigcap.sample

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.guardanis.sigcap.*
import com.guardanis.sigcap.androidx.showAppCompat

class MainActivity: AppCompatActivity(), SignatureEventListener {

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        setContentView(R.layout.activity_main)
    }

    override fun onPause() {
        FileCache.clear(this)

        super.onPause()
    }

    fun startClicked(view: View?) {
        SignatureDialogBuilder()
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                )
                .show(fragmentManager, "SignatureDialog")
    }

    fun startAppCompatClicked(view: View?) {
        SignatureDialogBuilder()
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                )
                .showAppCompat(supportFragmentManager, "SignatureDialog")
    }

    fun startStatelessClicked(view: View?) {
        SignatureDialogBuilder()
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                )
                .showStateless(this, this)
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