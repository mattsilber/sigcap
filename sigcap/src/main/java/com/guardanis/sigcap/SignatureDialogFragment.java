package com.guardanis.sigcap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.os.Build;

import com.guardanis.sigcap.paths.SignaturePathManager;

import java.lang.ref.WeakReference;

import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_PATH_MANAGER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_RENDERER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_REQUEST;

public class SignatureDialogFragment extends DialogFragment {

    public static final String DEFAULT_DIALOG_TAG = "signcap__Default_dialog";

    private SignatureRequest request = new SignatureRequest();
    private SignatureRenderer renderer;
    private SignaturePathManager pathManager;

    private WeakReference<SignatureEventListener> eventListener = new WeakReference<SignatureEventListener>(null);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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

        Dialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            if(!inputView.isSignatureInputAvailable())
                                throw new NoSignatureException("No signature found");

                            SignatureResponse saved = inputView.saveSignature();
                            SignatureEventListener listener = eventListener.get();

                            if (listener == null)
                                return;

                            listener.onSignatureEntered(saved);
                        }
                        catch(Exception e){
                            e.printStackTrace();

                            SignatureEventListener listener = eventListener.get();

                            if (listener == null)
                                return;

                            listener.onSignatureInputError(e);
                        }
                    }
                })
                .setNegativeButton(R.string.sig__default_dialog_action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SignatureEventListener listener = eventListener.get();

                        if (listener == null)
                            return;

                        listener.onSignatureInputCanceled();
                    }
                })
                .create();

        view.findViewById(R.id.sig__action_undo)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        inputView.undoLastSignaturePath();
                    }
                });

        return dialog;
    }

    protected View buildView(Activity activity){
        return activity.getLayoutInflater()
                .inflate(R.layout.sig__default_dialog, null, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY__SIGNATURE_REQUEST, request);
        outState.putParcelable(KEY__SIGNATURE_RENDERER, renderer);
        outState.putParcelable(KEY__SIGNATURE_PATH_MANAGER, pathManager);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        this.request = savedInstanceState.getParcelable(KEY__SIGNATURE_REQUEST);
        this.renderer = savedInstanceState.getParcelable(KEY__SIGNATURE_RENDERER);
        this.pathManager = savedInstanceState.getParcelable(KEY__SIGNATURE_PATH_MANAGER);
    }

    public SignatureDialogFragment setSignatureRequest(SignatureRequest request) {
        this.request = request;

        return this;
    }

    public SignatureDialogFragment setSignatureRenderer(SignatureRenderer renderer) {
        this.renderer = renderer;

        return this;
    }

    public SignatureDialogFragment setSignaturePathManager(SignaturePathManager pathManager) {
        this.pathManager = pathManager;

        return this;
    }

    public SignatureDialogFragment setSignatureEventListener(SignatureEventListener eventListener) {
        this.eventListener = new WeakReference<SignatureEventListener>(eventListener);

        return this;
    }
}
