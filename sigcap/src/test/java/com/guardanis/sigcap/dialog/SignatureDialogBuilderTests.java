package com.guardanis.sigcap.dialog;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.guardanis.sigcap.SignatureInputView;
import com.guardanis.sigcap.SignatureRenderer;
import com.guardanis.sigcap.SignatureRequest;
import com.guardanis.sigcap.paths.SignaturePathManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class SignatureDialogBuilderTests {

    @Test
    public void testCorrectlyBundlesArgumentsToStartDialogFragment() {
        SignatureRequest request = new SignatureRequest();
        SignatureRenderer renderer = new SignatureRenderer();
        SignaturePathManager pathManager = new SignaturePathManager();

        SignatureDialogBuilder builder = new SignatureDialogBuilder()
                .setAutoAttachEventListenerEnabled(false)
                .setRequest(request)
                .setRenderer(renderer)
                .setPathManager(pathManager);

        assertFalse(builder.isAutoAttachEventListenerEnabled());
        assertEquals(request, builder.getRequest());
        assertEquals(renderer, builder.getRenderer());
        assertEquals(pathManager, builder.getPathManager());

        Bundle arguments = builder.buildSignatureDialogArguments();

        assertFalse(arguments.getBoolean(SignatureDialogFragment.KEY__AUTO_ATTACH_EVENT_LISTENER));
        assertEquals(request, arguments.getParcelable(SignatureInputView.KEY__SIGNATURE_REQUEST));
        assertEquals(renderer, arguments.getParcelable(SignatureInputView.KEY__SIGNATURE_RENDERER));
        assertEquals(pathManager, arguments.getParcelable(SignatureInputView.KEY__SIGNATURE_PATH_MANAGER));
    }
}
