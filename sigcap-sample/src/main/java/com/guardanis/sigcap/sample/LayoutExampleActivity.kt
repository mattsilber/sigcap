package com.guardanis.sigcap.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.guardanis.sigcap.*

class LayoutExampleActivity: AppCompatActivity(), View.OnClickListener {

    val signatureInputView: SignatureInputView?
        get() = findViewById(R.id.sig__input_view)

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        setContentView(R.layout.activity_layout_example)

        val actions = listOf(
                R.id.layout_example_action_undo,
                R.id.layout_example_action_save
        )

        actions.forEach({
            findViewById<View>(it)
                    .setOnClickListener(this)
        })
    }

    override fun onClick(v: View?) {
        when (v?.id ?: 0) {
            R.id.layout_example_action_undo -> signatureInputView?.undoLastSignaturePath()
            R.id.layout_example_action_save -> {
                val signatureFile = signatureInputView
                        ?.takeIf(SignatureInputView::isSignatureInputAvailable)
                        ?.saveSignature()
                        ?.saveToFileCache(this)
                        ?.get()
                        ?: {
                            Toast.makeText(this, "Couldn't save signature", Toast.LENGTH_SHORT)
                                    .show()

                            null
                        }()
                        ?: return

                val result = Intent()
                result.putExtra(INTENT_RESULT_FILE, signatureFile)

                setResult(RESULT_OK, result)
                finish()
            }
        }
    }

    companion object {

        const val INTENT_RESULT_FILE = "signature_file"
    }
}