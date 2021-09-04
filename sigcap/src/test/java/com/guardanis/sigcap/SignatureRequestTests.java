package com.guardanis.sigcap;

import android.graphics.Color;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SignatureRequestTests {

    @Test
    public void testSignatureRequestCanBeRestoredFromParcel() {
        SignatureRequest request = new SignatureRequest()
                .setResultBackgroundColor(Color.BLACK)
                .setResultCropStrategy(SignatureRequest.ResultCropStrategy.SIGNATURE_BOUNDS)
                .setResultIncludeBaseline(true)
                .setResultIncludeBaselineXMark(true);

        SignatureRequest parcelledRequest = TestHelpers.parcelizeAndRecreate(
                request,
                SignatureRequest.CREATOR);

        assertEquals(Color.BLACK, parcelledRequest.getResultBackgroundColor());
        assertEquals(SignatureRequest.ResultCropStrategy.SIGNATURE_BOUNDS, parcelledRequest.getResultCropStrategy());
        assertEquals(true, parcelledRequest.shouldResultIncludeBaseline());
        assertEquals(true, parcelledRequest.shouldResultIncludeBaselineXMark());
    }
}
