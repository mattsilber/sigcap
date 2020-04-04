package com.guardanis.sigcap.dialog;

import android.view.View;

import com.guardanis.sigcap.SignatureInputView;

import java.lang.ref.WeakReference;

public class UndoLastSignatureClickListener implements View.OnClickListener {

    private WeakReference<SignatureInputView> inputView;

    public UndoLastSignatureClickListener(SignatureInputView inputView) {
        this.inputView = new WeakReference<SignatureInputView>(inputView);
    }

    @Override
    public void onClick(View v) {
        SignatureInputView view = inputView.get();

        if (view == null)
            return;

        view.undoLastSignaturePath();
    }
}
