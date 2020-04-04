package com.guardanis.sigcap.androidx

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.guardanis.sigcap.SignatureEventListener
import com.guardanis.sigcap.SignatureInputView
import com.guardanis.sigcap.SignatureInputView.*
import com.guardanis.sigcap.SignatureRenderer
import com.guardanis.sigcap.SignatureRequest
import com.guardanis.sigcap.dialog.SignatureDialogFragment.KEY__AUTO_ATTACH_EVENT_LISTENER
import com.guardanis.sigcap.dialog.events.DeferredSignatureEventDialogClickListener
import com.guardanis.sigcap.dialog.events.SignatureCanceledDialogClickListener
import com.guardanis.sigcap.dialog.events.SignatureSubmissionDialogClickListener
import com.guardanis.sigcap.dialog.events.UndoLastSignatureClickListener
import com.guardanis.sigcap.paths.SignaturePathManager
import java.lang.ref.WeakReference

/**
 * An [AppCompatDialogFragment] designed to manage a configurable [SignatureInputView]
 * in a state-restoring way.
 *
 * @author Yordan P. Dieguez
 * @author Matt Silber
 */
open class AppCompatSignatureDialogFragment: AppCompatDialogFragment(),
        DeferredSignatureEventDialogClickListener.EventListenerDelegate {

    private var request: SignatureRequest = SignatureRequest()
    private var renderer: SignatureRenderer? = null
    private var pathManager: SignaturePathManager? = null

    private var autoAttachEventListener = true

    private var eventListener: WeakReference<SignatureEventListener> = WeakReference<SignatureEventListener>(null)

    private val submissionActionClickListener = SignatureSubmissionDialogClickListener()
    private val canceledActionClickListener = SignatureCanceledDialogClickListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arguments = this.arguments ?: return

        this.request = arguments.getParcelable<SignatureRequest>(KEY__SIGNATURE_REQUEST) ?: this.request
        this.renderer = arguments.getParcelable(KEY__SIGNATURE_RENDERER)
        this.pathManager = arguments.getParcelable(KEY__SIGNATURE_PATH_MANAGER)
        this.autoAttachEventListener = arguments.getBoolean(KEY__AUTO_ATTACH_EVENT_LISTENER)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (!this.autoAttachEventListener) {
            Log.d(TAG, "Automated SignatureEventListener attaching disabled. You must manually set it.")

            return
        }

        this.eventListener = WeakReference<SignatureEventListener>(
                (parentFragment as? SignatureEventListener)
                        ?: (context as? SignatureEventListener)
                        ?: {
                            logOnAttachEventListenerFailed()

                            null
                        }())
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = this.activity
                ?: throw java.lang.RuntimeException("getActivity() cannot be null in onCreateDialog")

        val view: View = buildView(activity)

        val inputView = view.findViewById<SignatureInputView>(R.id.sig__input_view)
        inputView.signatureRequest = request
        inputView.signatureRenderer = renderer ?: inputView.signatureRenderer
        inputView.pathManager = pathManager ?: inputView.pathManager

        submissionActionClickListener.setEventListenerDelegate(this)
        submissionActionClickListener.setInputView(inputView)

        canceledActionClickListener.setEventListenerDelegate(this)

        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm, submissionActionClickListener)
                .setNegativeButton(R.string.sig__default_dialog_action_cancel, canceledActionClickListener)
                .create()

        view.findViewById<View>(R.id.sig__action_undo)
                .setOnClickListener(
                        UndoLastSignatureClickListener(inputView))

        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    protected fun buildView(activity: Activity): View {
        return activity.layoutInflater
                .inflate(com.guardanis.sigcap.R.layout.sig__default_dialog, null, false)
    }

    /**
     * Set the [SignatureEventListener] to be used when interacting with the
     * created [Dialog].
     *
     * Note: you will need to maintain a strong reference to your [SignatureEventListener]
     * as the [AppCompatSignatureDialogFragment] stores the instance in a [WeakReference].
     */
    fun setSignatureEventListener(eventListener: SignatureEventListener?): AppCompatSignatureDialogFragment {
        this.eventListener = WeakReference<SignatureEventListener>(eventListener)

        return this
    }

    override fun getEventListener(): SignatureEventListener? {
        return eventListener.get()
    }

    private fun logOnAttachEventListenerFailed() {
        val errorParentName = when (val parent = parentFragment) {
            null -> ""
            else -> " or $parent"
        }

        Log.d(TAG, """
            SignatureDialogFragment's $activity $errorParentName are 
            not a SignatureEventListener instance. You must manually set 
            one via setSignatureEventListener.
        """.trimIndent())
    }
}