package com.guardanis.sigcap.sample

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.guardanis.sigcap.exceptions.CanceledSignatureInputException
import com.guardanis.sigcap.exceptions.NoSignatureException
import java.io.File

class MainActivity: AppCompatActivity(), SignatureEventListener, View.OnClickListener {

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        setContentView(R.layout.activity_main)

        val actions = listOf(
                R.id.sample_action_fragment,
                R.id.sample_action_androidx_fragment,
                R.id.sample_action_stateless,
                R.id.sample_action_layout
        )

        actions.forEach({
            findViewById<View>(it)
                    .setOnClickListener(this)
        })
    }

    override fun onPause() {
        SignatureFileManager.deleteAll(this)
                .get()
                .also({
                    Log.d("SigcapSample", "Cleared all signature files from local storage: $it")
                })

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        attachFragmentManagerEventListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            data?.getSerializableExtra(LayoutExampleActivity.INTENT_RESULT_FILE)
                    ?.let({ it as? File })
                    ?.also(::showSignatureFile)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id ?: 0) {
            R.id.sample_action_fragment -> startClicked(v)
            R.id.sample_action_androidx_fragment -> startAppCompatClicked(v)
            R.id.sample_action_stateless -> startStatelessClicked(v)
            R.id.sample_action_layout -> startLayoutExampleActivity()
        }
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
                                .setBaselinePaintColor(Color.DKGRAY)
                                .setBaselineXMarkPaintColor(Color.GRAY))
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

    fun startLayoutExampleActivity() {
        val intent = Intent(this, LayoutExampleActivity::class.java)

        startActivityForResult(intent, 0)
    }

    override fun onSignatureEntered(response: SignatureResponse) {
//        val signatureResultImageView = findViewById<AppCompatImageView>(R.id.main__image)
//        signatureResultImageView.setImageBitmap(response.result)

        response.saveToFileCache(this)
                .get()
                .also(::showSignatureFile)
    }

    private fun showSignatureFile(file: File) {
        Log.d("SigcapSample", "Showing signature stored in: ${file.absolutePath}")

        val signatureResultImageView = findViewById<AppCompatImageView>(R.id.main__image)
        signatureResultImageView.setImageURI(Uri.fromFile(file))
    }

    override fun onSignatureInputError(e: Throwable) {
        when (e) {
            is NoSignatureException -> {
                // The user submitted a signature without actually drawing one
                Toast.makeText(this, "Signature submitted, but not entered", Toast.LENGTH_SHORT)
                        .show()
            }
            is CanceledSignatureInputException -> {
                // The user canceled the dialog without submitting anything
                Toast.makeText(this, "Signature input canceled", Toast.LENGTH_SHORT)
                        .show()
            }
            else -> {
                // Something weird happened
                Toast.makeText(this, "Signature error", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }
}