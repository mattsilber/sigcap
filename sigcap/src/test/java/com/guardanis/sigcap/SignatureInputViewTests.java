package com.guardanis.sigcap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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

        assertFalse(anotherInputView.isSignatureInputAvailable());

        anotherInputView.onRestoreInstanceState(restoredState);

        assert(anotherInputView.isSignatureInputAvailable());

        assertCoordinateHistoryEquals(
                anotherInputView,
                new Float[] { 1f, 1f, 20f, 20f, 10f, 10f });
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

    @Test
    public void testLastCompletePathCanBeUndone() {
        Context context = ApplicationProvider.getApplicationContext();

        SignatureInputView inputView = new SignatureInputView(context);

        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(1, 1, MotionEvent.ACTION_DOWN));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(10, 10, MotionEvent.ACTION_MOVE));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(5, 5, MotionEvent.ACTION_UP));

        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(2, 2, MotionEvent.ACTION_DOWN));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(20, 20, MotionEvent.ACTION_MOVE));
        inputView.onTouch(inputView, TestHelpers.generateMotionEvent(5, 5, MotionEvent.ACTION_UP));

        assertCoordinateHistoryEquals(
                inputView,
                new Float[] { 1f, 1f, 10f, 10f, 2f, 2f, 20f, 20f });

        inputView.undoLastSignaturePath();

        assertCoordinateHistoryEquals(
                inputView,
                new Float[] { 1f, 1f, 10f, 10f });
    }

    private void assertCoordinateHistoryEquals(SignatureInputView inputView, Float[] expected) {
        ArrayList<Float> serializedHistory = new ArrayList<Float>();

        for (SignaturePath path : inputView.getPathManager().getPaths()) {
            for (float value : path.serializeCoordinateHistory()) {
                serializedHistory.add(Float.valueOf(value));
            }
        }

        assertArrayEquals(expected, serializedHistory.toArray(new Float[0]));
    }

    @Test
    public void testBasicGettersSettersRetainInstance() {
        Context context = ApplicationProvider.getApplicationContext();

        SignatureInputView inputView = new SignatureInputView(context);
        SignatureRequest testRequest = new SignatureRequest();
        SignaturePathManager testManager = new SignaturePathManager();
        SignatureRenderer testRenderer = new SignatureRenderer();

        assertNotEquals(inputView.getSignatureRequest(), testRequest);
        assertNotEquals(inputView.getPathManager(), testManager);
        assertNotEquals(inputView.getSignatureRenderer(), testRenderer);

        inputView.setSignatureRequest(testRequest);
        inputView.setPathManager(testManager);
        inputView.setSignatureRenderer(testRenderer);

        assertEquals(inputView.getSignatureRequest(), testRequest);
        assertEquals(inputView.getPathManager(), testManager);
        assertEquals(inputView.getSignatureRenderer(), testRenderer);
    }
}
