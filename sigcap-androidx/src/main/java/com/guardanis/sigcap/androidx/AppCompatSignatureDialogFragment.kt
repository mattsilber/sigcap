package com.guardanis.sigcap.androidx

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.guardanis.sigcap.*
import com.guardanis.sigcap.SignatureInputView.*
import com.guardanis.sigcap.paths.SignaturePathManager
import java.lang.ref.WeakReference

/**
 * Dialog that prevent dismiss if device is rotated.
 * To use it, the activity or parent fragment must implement [SignatureEventListener] interface,
 * otherwise it will throw an [RuntimeException]
 *
 * @author Yordan P. Dieguez
 */
class AppCompatSignatureDialogFragment: AppCompatDialogFragment() {

    private var eventListener: WeakReference<SignatureEventListener> = WeakReference<SignatureEventListener>(null)

    private var request: SignatureRequest? = null
    private var renderer: SignatureRenderer? = null
    private var pathManager: SignaturePathManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.eventListener = WeakReference<SignatureEventListener>(when {
            parentFragment is SignatureEventListener -> {
                parentFragment as SignatureEventListener
            }
            context is SignatureEventListener -> {
                context
            }
            else -> {
                val errorParentName = when (val parent = parentFragment) {
                    null -> ""
                    else -> " or $parent"
                }

                Log.d(TAG, "SignatureDialogFragment's $activity $errorParentName are not a SignatureEventListener. You must manually set it.")

                null
            }
        })
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = this.activity
                ?: throw java.lang.RuntimeException("getActivity() cannot be null in onCreateDialog")

        val view: View = buildView(activity)

        val inputView = view.findViewById<SignatureInputView>(R.id.sig__input_view)
        inputView.signatureRequest = request ?: inputView.signatureRequest
        inputView.signatureRenderer = renderer ?: inputView.signatureRenderer
        inputView.pathManager = pathManager ?: inputView.pathManager

        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm, { _, _ ->
                    try {
                        if (!inputView.isSignatureInputAvailable)
                            throw NoSignatureException("No signature found")

                        val saved = inputView.saveSignature()

                        eventListener.get()
                                ?.onSignatureEntered(saved)
                    }
                    catch (e: Exception) {
                        e.printStackTrace()

                        eventListener.get()
                                ?.onSignatureInputError(e)
                    }
                })
                .setNegativeButton(R.string.sig__default_dialog_action_cancel, { _, _ ->
                    eventListener.get()
                            ?.onSignatureInputCanceled()
                })
                .create()

        view.findViewById<View>(R.id.sig__action_undo)
                .setOnClickListener({ inputView.undoLastSignaturePath() })

        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun buildView(activity: Activity): View {
        return activity.layoutInflater
                .inflate(com.guardanis.sigcap.R.layout.sig__default_dialog, null, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(KEY__SIGNATURE_REQUEST, request)
        outState.putParcelable(KEY__SIGNATURE_RENDERER, renderer)
        outState.putParcelable(KEY__SIGNATURE_PATH_MANAGER, pathManager)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        this.request = savedInstanceState?.getParcelable(KEY__SIGNATURE_REQUEST)
        this.renderer = savedInstanceState?.getParcelable(KEY__SIGNATURE_RENDERER)
        this.pathManager = savedInstanceState?.getParcelable(KEY__SIGNATURE_PATH_MANAGER)
    }

    /**
     * Pass an [SignatureRequest] object before show the dialog.
     * @param request [SignatureRequest] object
     * @return [SignatureDialogFragment]
     * @author Yordan P. Dieguez
     */
    fun setSignatureRequest(request: SignatureRequest): AppCompatSignatureDialogFragment {
        this.request = request

        return this
    }

    /**
     * Pass an [SignatureRenderer] object before show the dialog.
     * @param renderer [SignatureRenderer] object
     * @return [SignatureDialogFragment]
     * @author Yordan P. Dieguez
     */
    fun setSignatureRenderer(renderer: SignatureRenderer?): AppCompatSignatureDialogFragment {
        this.renderer = renderer

        return this
    }

    fun setSignaturePathManager(pathManager: SignaturePathManager?): AppCompatSignatureDialogFragment {
        this.pathManager = pathManager

        return this
    }

    fun setSignatureEventListener(eventListener: SignatureEventListener?): AppCompatSignatureDialogFragment {
        this.eventListener = WeakReference<SignatureEventListener>(eventListener)

        return this
    }
}