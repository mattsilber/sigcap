package com.guardanis.sigcap;

import android.view.MotionEvent;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.guardanis.sigcap.paths.SignaturePathManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.guardanis.sigcap.TestHelpers.generateMotionEvent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class SignatureTouchDelegateTests {

    private MotionEvent actionDownEvent = generateMotionEvent(0f, 0f, MotionEvent.ACTION_DOWN);

    @Test
    public void testSignatureTouchDelegateActionUpDelegatesToOnTouchUp() {
        SignaturePathManager manager = spy(SignaturePathManager.class);
        MotionEvent motionEvent = generateMotionEvent(0f, 0f, MotionEvent.ACTION_UP);

        SignatureTouchDelegate delegate = new SignatureTouchDelegate();
        delegate.delegate(mock(View.class), actionDownEvent, manager);
        delegate.delegate(mock(View.class), motionEvent, manager);

        verify(manager, times(1))
                .notifyTouchDown(any(MotionEvent.class));

        verify(manager, never())
                .notifyTouchMove(any(MotionEvent.class));

        verify(manager, times(1))
                .notifyTouchUp(any(MotionEvent.class));
    }

    @Test
    public void testSignatureTouchDelegateNotifiesTouchMoveForAllOtherActions() {
        verifyMoveDelegate(MotionEvent.ACTION_MOVE);
        verifyMoveDelegate(MotionEvent.ACTION_POINTER_DOWN);
        verifyMoveDelegate(MotionEvent.ACTION_OUTSIDE);
    }

    private void verifyMoveDelegate(int action) {
        SignaturePathManager manager = spy(SignaturePathManager.class);
        MotionEvent motionEvent = generateMotionEvent(0f, 0f, action);

        SignatureTouchDelegate delegate = new SignatureTouchDelegate();
        delegate.delegate(mock(View.class), actionDownEvent, manager);
        delegate.delegate(mock(View.class), motionEvent, manager);

        verify(manager, times(1))
                .notifyTouchDown(any(MotionEvent.class));

        verify(manager, times(1))
                .notifyTouchMove(any(MotionEvent.class));

        verify(manager, never())
                .notifyTouchUp(any(MotionEvent.class));
    }
}
