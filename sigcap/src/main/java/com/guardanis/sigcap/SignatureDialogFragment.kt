package com.guardanis.sigcap

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Path
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import com.guardanis.sigcap.viewmodel.SignatureViewModel

/**
 * Dialog that prevent dismiss if device is rotated.
 * To use it, the activity or parent fragment must implement [SignatureEventListener] interface,
 * otherwise it will throw an [RuntimeException]
 *
 * @author Yordan P. Dieguez
 */
class SignatureDialogFragment : AppCompatDialogFragment() {

    private lateinit var eventListener: SignatureEventListener
    private lateinit var inputView: SignatureInputView
    private val viewModel: SignatureViewModel by viewModels()

    private var request: SignatureRequest? = null
    private var renderer: SignatureRenderer? = null
    private var signaturePaths: List<List<Path>>? = null

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

        request = request ?: viewModel.request
        renderer = renderer ?: viewModel.renderer
        signaturePaths = signaturePaths ?: viewModel.signaturePaths
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View? = activity?.layoutInflater?.inflate(R.layout.sig__default_dialog, null, false)

        inputView = view?.findViewById<View>(R.id.sig__input_view) as SignatureInputView
        request?.apply { inputView.signatureRequest = this }
        renderer?.apply { inputView.signatureRenderer = this }
        signaturePaths?.apply { inputView.signaturePaths = this }

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

        view.findViewById<View>(R.id.sig__action_undo).setOnClickListener {
            inputView.undoLastSignaturePath()
        }

        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.signaturePaths = inputView.signaturePaths
        viewModel.request = inputView.signatureRequest
        viewModel.renderer = inputView.signatureRenderer
    }

    /**
     * Pass an [SignatureRequest] object before show the dialog.
     * @param request [SignatureRequest] object
     * @return [SignatureDialogFragment]
     * @author Yordan P. Dieguez
     */
    fun setRequest(request: SignatureRequest): SignatureDialogFragment {
        this.request = request
        return this
    }

    /**
     * Pass an [SignatureRenderer] object before show the dialog.
     * @param renderer [SignatureRenderer] object
     * @return [SignatureDialogFragment]
     * @author Yordan P. Dieguez
     */
    fun setSignatureRenderer(renderer: SignatureRenderer?): SignatureDialogFragment {
        this.renderer = renderer
        return this
    }
}