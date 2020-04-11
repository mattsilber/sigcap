package com.guardanis.sigcap.dialog.events;

import android.content.DialogInterface;

import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.SignatureInputView;
import com.guardanis.sigcap.SignatureResponse;
import com.guardanis.sigcap.exceptions.NoSignatureException;

public class SignatureSubmissionDialogClickListener extends DeferredSignatureEventDialogClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        SignatureInputView inputView = this.inputView.get();
        SignatureEventListener eventListener = this.getSignatureEventListener();

        if (inputView == null)
            return;

        try {
            if(!inputView.isSignatureInputAvailable())
                throw new NoSignatureException("No signature found");

            SignatureResponse saved = inputView.saveSignature();

            if (eventListener == null)
                return;

            eventListener.onSignatureEntered(saved);
        }
        catch(Exception e){
            e.printStackTrace();

            if (eventListener == null)
                return;

            eventListener.onSignatureInputError(e);
        }
    }
}
