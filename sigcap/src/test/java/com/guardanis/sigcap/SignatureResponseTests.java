package com.guardanis.sigcap;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class SignatureResponseTests {

    @Test
    public void testSignatureResponseSavesBitmapToFile() {
        Bitmap bitmapResult = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        SignatureResponse response = new SignatureResponse(bitmapResult);

        assertEquals(bitmapResult, response.getResult());

        Context context = mock(Context.class);

        File fileResult = null;

        try {
            fileResult = response.saveToFileCache(context)
                    .get();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(fileResult);
        assert(fileResult.getPath().matches("sigcap\\\\sig\\d+\\.png"));
    }
}
