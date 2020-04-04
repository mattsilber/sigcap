package com.guardanis.sigcap.dialog.events;

import android.content.DialogInterface;

import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.SignatureInputView;

import java.lang.ref.WeakReference;

public abstract class DeferredSignatureEventDialogClickListener implements DialogInterface.OnClickListener {

    public interface EventListenerDelegate {
        public SignatureEventListener getSignatureEventListener();
    }

    protected WeakReference<SignatureInputView> inputView = new WeakReference<SignatureInputView>(null);
    private WeakReference<EventListenerDelegate> delegate = new WeakReference<EventListenerDelegate>(null);

    public DeferredSignatureEventDialogClickListener setInputView(SignatureInputView inputView) {
        this.inputView = new WeakReference<SignatureInputView>(inputView);

        return this;
    }

    public DeferredSignatureEventDialogClickListener setEventListenerDelegate(EventListenerDelegate delegate) {
        this.delegate = new WeakReference<EventListenerDelegate>(delegate);

        return this;
    }

    protected SignatureEventListener getSignatureEventListener() {
        EventListenerDelegate delegate = this.delegate.get();

        if (delegate == null)
            return null;

        return delegate.getSignatureEventListener();
    }
}
