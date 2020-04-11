package com.guardanis.sigcap.dialog.events;

import android.content.DialogInterface;

import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.exceptions.CanceledSignatureInputException;

public class SignatureCanceledDialogClickListener extends DeferredSignatureEventDialogClickListener {

    @Override
    public void onClick(DialogInterface dialog, int which) {
        SignatureEventListener eventListener = this.getSignatureEventListener();

        if (eventListener == null)
            return;

        eventListener.onSignatureInputError(
                new CanceledSignatureInputException());
    }
}
