package com.guardanis.sigcap.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.SignatureInputView;
import com.guardanis.sigcap.SignatureRequest;
import com.guardanis.sigcap.SignatureResponse;
import com.guardanis.sigcap.TestHelpers;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class SignatureDialogFragmentTests {

    @Test
    public void testAutoAttachesListenerIfContextIsSignatureEventListenerAndEnabled() {
        Activity activity = new TestActivity();
        SignatureRequest request = new SignatureRequest();

        Bundle arguments = new Bundle();
        arguments.putBoolean(SignatureDialogFragment.KEY__AUTO_ATTACH_EVENT_LISTENER, true);
        arguments.putParcelable(SignatureInputView.KEY__SIGNATURE_REQUEST, request);

        SignatureDialogFragment fragment = new SignatureDialogFragment();
        fragment.setArguments(arguments);
        fragment.onCreate(null);

        assertNull(fragment.getSignatureEventListener());

        fragment.onAttach((Context) activity);

        assertEquals(activity, fragment.getSignatureEventListener());
    }

    @Test
    public void testDoesNotAutoAttachListenerIfContextIsSignatureEventListenerAndDisabled() {
        Activity activity = new TestActivity();
        SignatureRequest request = new SignatureRequest();

        Bundle arguments = new Bundle();
        arguments.putBoolean(SignatureDialogFragment.KEY__AUTO_ATTACH_EVENT_LISTENER, false);
        arguments.putParcelable(SignatureInputView.KEY__SIGNATURE_REQUEST, request);

        SignatureDialogFragment fragment = new SignatureDialogFragment();
        fragment.setArguments(arguments);
        fragment.onCreate(null);

        assertNull(fragment.getSignatureEventListener());

        fragment.onAttach((Context) activity);

        assertNull(fragment.getSignatureEventListener());

        fragment.setSignatureEventListener((SignatureEventListener) activity);

        assertEquals(activity, fragment.getSignatureEventListener());
    }

    private class TestActivity extends Activity implements SignatureEventListener {


        @Override
        public void onSignatureEntered(SignatureResponse response) {

        }

        @Override
        public void onSignatureInputError(Throwable error) {

        }
    }
}
