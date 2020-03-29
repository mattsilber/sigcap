package com.guardanis.sigcap

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import com.guardanis.sigcap.viewmodel.SignatureViewModel

class SignatureDialogFragment : AppCompatDialogFragment() {

    private lateinit var eventListener: SignatureEventListener
    private lateinit var inputView: SignatureInputView
    private val viewModel: SignatureViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        eventListener = when {
            parentFragment is SignatureEventListener -> {
                parentFragment as SignatureEventListener
            }
            context is SignatureEventListener -> {
                context
            }
            else -> {
                val s = if (parentFragment != null) {
                    " or $parentFragment"
                } else {
                    ""
                }

                throw RuntimeException("$context $s must implement SignatureEventListener")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View? = buildView()

        inputView = view?.findViewById<View>(R.id.sig__input_view) as SignatureInputView
        inputView.signaturePaths = viewModel.signaturePaths

        view.findViewById<View>(R.id.sig__action_undo).setOnClickListener {
            inputView.undoLastSignaturePath()
        }

        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm) { _, _ ->
                    try {
                        if (!inputView.isSignatureInputAvailable)
                            throw NoSignatureException("No signature found")
                        val saved = inputView.saveSignature()
                        eventListener.onSignatureEntered(saved)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        eventListener.onSignatureInputError(e)
                    }
                }
                .setNegativeButton(R.string.sig__default_dialog_action_cancel) { _, _ ->
                    eventListener.onSignatureInputCanceled()
                }
                .create()

        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.signaturePaths = inputView.signaturePaths
    }

    @SuppressLint("InflateParams")
    private fun buildView(): View? {
        return requireActivity().layoutInflater.inflate(R.layout.sig__default_dialog, null, false)
    }
}