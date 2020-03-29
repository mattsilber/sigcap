package com.guardanis.sigcap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class SignatureDialogBuilder {

    private SignatureRequest request = new SignatureRequest();
    private SignatureRenderer renderer;

    public SignatureDialogBuilder setRequest(SignatureRequest request) {
        this.request = request;

        return this;
    }

    public SignatureDialogBuilder setSignatureRenderer(SignatureRenderer renderer) {
        this.renderer = renderer;

        return this;
    }

    public void show(Activity activity, final SignatureEventListener eventListener) {
        final View view = buildView(activity);

        final SignatureInputView inputView = view.findViewById(R.id.sig__input_view);
        inputView.setSignatureRequest(request);

        if (renderer != null) {
            inputView.setSignatureRenderer(renderer);
        }

        new AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(
                        R.string.sig__default_dialog_action_confirm,
                        (dialogInterface, i) -> {
                            try {
                                if (!inputView.isSignatureInputAvailable())
                                    throw new NoSignatureException("No signature found");

                                SignatureResponse saved = inputView.saveSignature();

                                eventListener.onSignatureEntered(saved);
                            } catch (Exception e) {
                                e.printStackTrace();

                                eventListener.onSignatureInputError(e);
                            }
                        })
                .setNegativeButton(
                        R.string.sig__default_dialog_action_cancel,
                        (dialogInterface, i) -> eventListener.onSignatureInputCanceled()
                )
                .show();

        view.findViewById(R.id.sig__action_undo).setOnClickListener(
                view1 -> inputView.undoLastSignaturePath()
        );
    }

    @SuppressLint("InflateParams")
    private View buildView(@NotNull Activity activity) {
        return activity.getLayoutInflater()
                .inflate(R.layout.sig__default_dialog, null, false);
    }
}
