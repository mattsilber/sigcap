package com.guardanis.sigcap;

import android.view.MotionEvent;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.guardanis.sigcap.SignaturePathTests.joinToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class SignaturePathManagerTests {

    @Test
    public void testPathManagerAddsEligibleActivePathToHistoryOnTouchDown() {
        MotionEvent motionEvent = generateMotionEvent(0f, 0f, MotionEvent.ACTION_DOWN);

        SignaturePathManager manager = new SignaturePathManager();

        SignaturePath activePath = manager.getActivePath();
        activePath.movePathTo(motionEvent);
        activePath.addPathLineTo(motionEvent);

        assertEquals(0, manager.getPaths().size());

        manager.notifyTouchDown(motionEvent);

        assertEquals(1, manager.getPaths().size());
    }

    @Test
    public void testPathManagerDoesNotAddIneligibleActivePathToHistoryOnTouchDown() {
        MotionEvent motionEvent = generateMotionEvent(0f, 0f, MotionEvent.ACTION_DOWN);

        SignaturePathManager manager = new SignaturePathManager();

        assertEquals(0, manager.getPaths().size());

        manager.notifyTouchDown(motionEvent);

        assertEquals(0, manager.getPaths().size());
    }

    @Test
    public void testPathManagerAddsActivePathToHistoryOnTouchUp() {
        MotionEvent motionEvent = generateMotionEvent(0f, 0f, MotionEvent.ACTION_MOVE);

        SignaturePathManager manager = new SignaturePathManager();

        SignaturePath activePath = manager.getActivePath();
        activePath.movePathTo(motionEvent);

        assertEquals(0, manager.getPaths().size());
        assertEquals(1, manager.getActivePath().getCoordinateHistorySize());

        assertEquals(0, manager.getClonedPaths().size());
        assertEquals(1, manager.getClonedActivePath().getCoordinateHistorySize());

        manager.notifyTouchMove(motionEvent);
        manager.notifyTouchMove(motionEvent);
        manager.notifyTouchMove(motionEvent);

        assertEquals(0, manager.getPaths().size());
        assertEquals(4, manager.getActivePath().getCoordinateHistorySize());

        assertEquals(0, manager.getClonedPaths().size());
        assertEquals(4, manager.getClonedActivePath().getCoordinateHistorySize());
    }

    @Test
    public void testPathManagerUpdatesActivePathOnTouchMove() {
        MotionEvent motionEvent = generateMotionEvent(0f, 0f, MotionEvent.ACTION_UP);

        SignaturePathManager manager = new SignaturePathManager();

        assertEquals(0, manager.getPaths().size());

        manager.notifyTouchUp(motionEvent);

        assertEquals(1, manager.getPaths().size());
    }

    @Test
    public void testSignaturePathManagerCalculatedMinMaxBounds() {
        SignaturePathManager manager = new SignaturePathManager()
                .addPath(new SignaturePath(new float[] { 0f, 0f, 10f, 100f, -25f, -5f }))
                .addPath(new SignaturePath(new float[] { 10f, -10f, -10f, -10f }))
                .addPath(new SignaturePath(new float[] { 100f, -100f, 10f, 100f  }));

        assertEquals(
                "-25.0,-100.0,100.0,100.0",
                joinToString(manager.getMinMaxBounds()));
    }

    private MotionEvent generateMotionEvent(float x, float y, int action) {
        MotionEvent motionEvent = mock(MotionEvent.class);

        Mockito.when(motionEvent.getX())
                .thenReturn(x);

        Mockito.when(motionEvent.getY())
                .thenReturn(y);

        Mockito.when(motionEvent.getAction())
                .thenReturn(action);

        return motionEvent;
    }
}
