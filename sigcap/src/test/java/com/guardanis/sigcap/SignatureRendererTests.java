package com.guardanis.sigcap;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SignatureRendererTests {

    @Test
    public void testSignatureRendererCanBeRestoredFromParcel() {
        SignatureRenderer renderer = new SignatureRenderer()
                .setBaselinePaddingBottom(1)
                .setBaselinePaddingHorizontal(2)
                .setBaselinePaintColor(Color.RED)
                .setBaselineStrokeWidth(3)
                .setBaselineXMarkLength(4)
                .setBaselineXMarkOffsetHorizontal(5)
                .setBaselineXMarkOffsetVertical(6)
                .setBaselineXMarkPaintColor(Color.BLUE)
                .setBaselineXMarkStrokeWidth(7)
                .setSignaturePaintColor(Color.GRAY)
                .setSignatureStrokeWidth(8);

        SignatureRenderer parcelledRenderer = TestHelpers.parcelizeAndRecreate(
                renderer,
                SignatureRenderer.CREATOR);

        assertEquals(1, parcelledRenderer.getBaselinePaddingBottom());
        assertEquals(2, parcelledRenderer.getBaselinePaddingHorizontal());
        assertEquals(Color.RED, parcelledRenderer.getBaselinePaintColor());
        assertEquals(3, parcelledRenderer.getBaselineStrokeWidth());
        assertEquals(4, parcelledRenderer.getBaselineXMarkLength());
        assertEquals(5, parcelledRenderer.getBaselineXMarkOffsetHorizontal());
        assertEquals(6, parcelledRenderer.getBaselineXMarkOffsetVertical());
        assertEquals(Color.BLUE, parcelledRenderer.getBaselineXMarkPaintColor());
        assertEquals(7, parcelledRenderer.getBaselineXMarkStrokeWidth());
        assertEquals(Color.GRAY, parcelledRenderer.getSignaturePaintColor());
        assertEquals(8, parcelledRenderer.getSignatureStrokeWidth());
    }

    @Test
    public void testRendersBitmapWithCanvasBounds() {
        SignatureRequest request = new SignatureRequest()
                .setResultCropStrategy(SignatureRequest.ResultCropStrategy.CANVAS_BOUNDS);

        SignaturePathManager manager = new SignaturePathManager();
        SignatureRenderer renderer = new SignatureRenderer();

        Bitmap result = renderer.renderToBitmap(request, manager, new int[] { 100, 50 });

        assertEquals(100, result.getWidth());
        assertEquals(50, result.getHeight());
    }

    @Test
    public void testRendersBitmapWithSignatureMinMaxBounds() {
        SignatureRequest request = new SignatureRequest()
                .setResultCropStrategy(SignatureRequest.ResultCropStrategy.SIGNATURE_BOUNDS);

        SignaturePathManager manager = new SignaturePathManager()
                .addPath(new SignaturePath(new float[] { 0f, 0f, 50f, 100f }));

        SignatureRenderer renderer = new SignatureRenderer()
                .setSignatureStrokeWidth(0);

        Bitmap result = renderer.renderToBitmap(request, manager, new int[] { 100, 50 });

        assertEquals(50, result.getWidth());
        assertEquals(100, result.getHeight());
    }
}
