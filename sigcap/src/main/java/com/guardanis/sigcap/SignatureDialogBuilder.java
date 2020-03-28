package com.guardanis.sigcap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import java.io.File;

public class SignatureDialogBuilder {

    public interface SignatureEventListener {
        public void onSignatureEntered(File savedFile);
        public void onSignatureInputCanceled();
        public void onSignatureInputError(Throwable e);
    }

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

        final SignatureInputView inputView = (SignatureInputView) view.findViewById(R.id.sig__input_view);
        inputView.setSignatureRequest(request);

        if (renderer != null) {
            inputView.setSignatureRenderer(renderer);
        }

        new AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            if(!inputView.isSignatureInputAvailable())
                                throw new NoSignatureException("No signature found");

                            File saved = inputView.saveSignature();

                            eventListener.onSignatureEntered(saved);
                        }
                        catch(Exception e){
                            e.printStackTrace();

                            eventListener.onSignatureInputError(e);
                        }
                    }
                })
                .setNegativeButton(R.string.sig__default_dialog_action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eventListener.onSignatureInputCanceled();
                    }
                })
                .show();

        view.findViewById(R.id.sig__action_undo)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        inputView.undoLastSignaturePath();
                    }
                });
    }

    protected View buildView(Activity activity){
        return activity.getLayoutInflater()
                .inflate(R.layout.sig__default_dialog, null, false);
    }
}
