package com.guardanis.sigcap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.Robolectric;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

@RunWith(AndroidJUnit4.class)
public class SignatureInputViewTests {

    @Test
    public void testSignatureInputViewCanBeRestoredFromParcel() {
        Context context = ApplicationProvider.getApplicationContext();

        SignatureInputView inputView = new SignatureInputView(context);

        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(1, 1, MotionEvent.ACTION_DOWN));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(20, 20, MotionEvent.ACTION_MOVE));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(10, 10, MotionEvent.ACTION_MOVE));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(5, 5, MotionEvent.ACTION_UP));

        SignatureInputView.SignatureState inputViewParcel = (SignatureInputView.SignatureState) inputView.onSaveInstanceState();
        SignatureInputView.SignatureState restoredState = TestHelpers.parcelizeAndRecreate(
                inputViewParcel,
                SignatureInputView.SignatureState.CREATOR);

        SignatureInputView anotherInputView = new SignatureInputView(context);

        assertFalse(anotherInputView.getPathManager().isSignatureInputAvailable());

        anotherInputView.onRestoreInstanceState(restoredState);

        assert(anotherInputView.getPathManager().isSignatureInputAvailable());
        assertArrayEquals(new float[] { 1, 1, 20, 20, 10, 10 }, anotherInputView.getPathManager().getPaths().get(0).serializeCoordinateHistory(), 0);
    }

    @Test
    public void testSignatureInputViewDrawsPathsComponentsInOrder() {
        Context context = ApplicationProvider.getApplicationContext();
        final SignatureRenderer renderer = spy(SignatureRenderer.class);

        AttributeSet attributes = Robolectric
                .buildAttributeSet()
                .build();

        SignatureInputView inputView = new SignatureInputView(context, attributes) {
            @Override
            protected SignatureRenderer generateSignatureRenderer(TypedArray attributes) {
                return renderer;
            }
        };

        Canvas canvas = mock(Canvas.class);

        inputView.onDraw(canvas);

        InOrder orderedVerifications = inOrder(renderer, renderer, renderer);

        orderedVerifications.verify(renderer, times(1))
                .drawBaseline(canvas);

        orderedVerifications.verify(renderer, times(1))
                .drawBaselineXMark(canvas);

        orderedVerifications.verify(renderer, times(1))
                .drawPathManager(canvas, inputView.getPathManager());
    }
}
