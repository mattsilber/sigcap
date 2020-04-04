package com.guardanis.sigcap.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.guardanis.sigcap.R;
import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.SignatureInputView;
import com.guardanis.sigcap.SignatureRenderer;
import com.guardanis.sigcap.SignatureRequest;
import com.guardanis.sigcap.dialog.events.DeferredSignatureEventDialogClickListener;
import com.guardanis.sigcap.dialog.events.SignatureCanceledDialogClickListener;
import com.guardanis.sigcap.dialog.events.SignatureSubmissionDialogClickListener;
import com.guardanis.sigcap.dialog.events.UndoLastSignatureClickListener;
import com.guardanis.sigcap.paths.SignaturePathManager;

import java.lang.ref.WeakReference;

import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_PATH_MANAGER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_RENDERER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_REQUEST;

/**
 * A {@link DialogFragment} designed to manage a configurable {@link SignatureInputView}
 * in a state-restoring way.
 */
public class SignatureDialogFragment extends DialogFragment
        implements DeferredSignatureEventDialogClickListener.EventListenerDelegate {

    public static final String DEFAULT_DIALOG_TAG = "signcap__default_dialog";

    public static final String KEY__AUTO_ATTACH_EVENT_LISTENER = "signcap__auto_attach_event_listsner";

    private SignatureRequest request = new SignatureRequest();
    private SignatureRenderer renderer;
    private SignaturePathManager pathManager;

    private boolean autoAttachEventListener = true;

    private WeakReference<SignatureEventListener> eventListener = new WeakReference<SignatureEventListener>(null);

    private SignatureSubmissionDialogClickListener submissionActionClickListener = new SignatureSubmissionDialogClickListener();
    private SignatureCanceledDialogClickListener canceledActionClickListener = new SignatureCanceledDialogClickListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        SignatureRequest request = arguments.getParcelable(KEY__SIGNATURE_REQUEST);

        if (request != null) {
            this.request = request;
        }

        this.renderer = arguments.getParcelable(KEY__SIGNATURE_RENDERER);
        this.pathManager = arguments.getParcelable(KEY__SIGNATURE_PATH_MANAGER);
        this.autoAttachEventListener = arguments.getBoolean(KEY__AUTO_ATTACH_EVENT_LISTENER);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!this.autoAttachEventListener) {
            Log.d(SignatureInputView.TAG, "Automated SignatureEventListener attaching disabled. You must manually set it.");

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Fragment parent = getParentFragment();

            if (parent instanceof SignatureEventListener) {
                this.eventListener = new WeakReference<SignatureEventListener>((SignatureEventListener) parent);

                return;
            }
        }

        if (context instanceof SignatureEventListener) {
            this.eventListener = new WeakReference<SignatureEventListener>((SignatureEventListener) context);

            return;
        }

        Log.d(SignatureInputView.TAG, "SignatureDialogFragment's Activity or parent Fragment are not a SignatureEventListener. You must manually set it.");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();

        if (activity == null)
            throw new RuntimeException("getActivity() cannot be null in onCreateDialog");

        final View view = buildView(activity);

        final SignatureInputView inputView = (SignatureInputView) view.findViewById(R.id.sig__input_view);
        inputView.setSignatureRequest(request);

        if (renderer != null) {
            inputView.setSignatureRenderer(renderer);
        }

        if (pathManager != null) {
            inputView.setPathManager(pathManager);
        }

        submissionActionClickListener.setEventListenerDelegate(this);
        submissionActionClickListener.setInputView(inputView);

        canceledActionClickListener.setEventListenerDelegate(this);

        Dialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm, submissionActionClickListener)
                .setNegativeButton(R.string.sig__default_dialog_action_cancel, canceledActionClickListener)
                .create();

        view.findViewById(R.id.sig__action_undo)
                .setOnClickListener(
                        new UndoLastSignatureClickListener(inputView));

        return dialog;
    }

    protected View buildView(Activity activity){
        return activity.getLayoutInflater()
                .inflate(R.layout.sig__default_dialog, null, false);
    }

    /**
     * Set the {@link SignatureEventListener} to be used when interacting with the
     * created {@link Dialog}.
     *
     * Note: you will need to maintain a strong reference to your {@link SignatureEventListener}
     * as the {@link SignatureDialogFragment} stores the instance in a {@link WeakReference}.
     */
    public SignatureDialogFragment setSignatureEventListener(SignatureEventListener eventListener) {
        this.eventListener = new WeakReference<SignatureEventListener>(eventListener);

        return this;
    }

    @Override
    public SignatureEventListener getSignatureEventListener() {
        return eventListener.get();
    }
}
