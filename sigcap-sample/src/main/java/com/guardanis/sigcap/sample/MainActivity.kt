package com.guardanis.sigcap.sample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.guardanis.sigcap.*
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

        attachFragmentManagerEventListener()
    }

    fun startClicked(view: View?) {
        SignatureDialogBuilder()
                // Optionally set a SignatureRequest to configure the
                // result rendering options
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultIncludeBaseline(true)
                                .setResultIncludeBaselineXMark(true)
                                .setResultCropStrategy(SignatureRequest.ResultCropStrategy.CANVAS_BOUNDS))
                // Optionally set a SignatureRenderer to manually configure the
                // rendering instance, or supply your own.
                .setRenderer(
                        SignatureRenderer(resources)
                                .setSignaturePaintColor(Color.BLACK)
                                .setBaselinePaintColor(Color.BLACK))
                // Optionally disable automatically attaching the Activity/Fragment
                // as the SignatureEventListsner instance when re-attaching
                .setAutoAttachEventListenerEnabled(false)
                // Show the dialog with the default Fragment tag and no SignatureEventListener
                .showDialogFragment(fragmentManager)

        // Since we're using showDialogFragment(FragmentManager) and not
        // showDialogFragment(FragmentManager, String, SignatureEventListener),
        // we need to manually attach the event listener to the Fragment
        attachFragmentManagerEventListener()
    }

    private fun attachFragmentManagerEventListener() {
        // If our DialogFragment exists, we must reset the SignatureEventListener
        fragmentManager.findFragmentByTag(SignatureDialogFragment.DEFAULT_DIALOG_TAG)
                ?.let({ it as? SignatureDialogFragment })
                ?.setSignatureEventListener(this)
    }

    fun startAppCompatClicked(view: View?) {
        SignatureDialogBuilder()
                // Optionally set a SignatureRequest to configure the
                // result rendering options
                .setRequest(
                        SignatureRequest()
                                .setResultBackgroundColor(Color.TRANSPARENT)
                                .setResultCropStrategy(SignatureRequest.ResultCropStrategy.SIGNATURE_BOUNDS))
                .showAppCompatDialogFragment(supportFragmentManager, eventListener = this)
    }

    fun startStatelessClicked(view: View?) {
        SignatureDialogBuilder()
                .showStatelessAlertDialog(this, this)
    }

    override fun onSignatureEntered(response: SignatureResponse) {
        findViewById<AppCompatImageView>(R.id.main__image)
                .setImageBitmap(response.result)

        val savedFile = response.saveToFileCache(this)
                .get()
                .also({
                    Log.d("SigcapSample", "Signature stored in: ${it.absolutePath}")
                })
    }

    override fun onSignatureInputCanceled() {
        Toast.makeText(this, "Signature input canceled", Toast.LENGTH_SHORT)
                .show()
    }

    override fun onSignatureInputError(e: Throwable) {
        if (e is NoSignatureException) {
            Toast.makeText(this, "Signature not entered", Toast.LENGTH_SHORT)
                    .show()
        }

        Toast.makeText(this, "Signature error", Toast.LENGTH_SHORT)
                .show()
    }
}