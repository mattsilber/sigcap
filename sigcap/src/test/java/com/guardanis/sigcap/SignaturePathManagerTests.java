package com.guardanis.sigcap;

import android.view.MotionEvent;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.guardanis.sigcap.SignaturePathTests.joinToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class SignaturePathManagerTests {

    @Test
    public void testPathManagerAddsEligibleActivePathToHistoryOnTouchDown() {
        SignaturePathManager manager = new SignaturePathManager();

        SignaturePath activePath = manager.getActivePath();
        activePath.startPathAt(0, 0);
        activePath.addPathLineTo(0, 0);

        assertEquals(0, manager.getPaths().size());

        MotionEvent motionEvent = TestHelpers.generateMotionEvent(0f, 0f, MotionEvent.ACTION_DOWN);

        manager.notifyTouchDown(motionEvent);

        assertEquals(1, manager.getPaths().size());
    }

    @Test
    public void testPathManagerDoesNotAddIneligibleActivePathToHistoryOnTouchDown() {
        MotionEvent motionEvent = TestHelpers.generateMotionEvent(0f, 0f, MotionEvent.ACTION_DOWN);

        SignaturePathManager manager = new SignaturePathManager();

        assertEquals(0, manager.getPaths().size());

        manager.notifyTouchDown(motionEvent);

        assertEquals(0, manager.getPaths().size());
    }

    @Test
    public void testPathManagerAddsActivePathToHistoryOnTouchUp() {
        SignaturePathManager manager = new SignaturePathManager();

        SignaturePath activePath = manager.getActivePath();
        activePath.startPathAt(0, 0);

        assertEquals(0, manager.getPaths().size());
        assertEquals(1, manager.getActivePath().getCoordinateHistorySize());

        assertEquals(0, manager.getClonedPaths().size());
        assertEquals(1, manager.getClonedActivePath().getCoordinateHistorySize());

        MotionEvent motionEvent = TestHelpers.generateMotionEvent(0f, 0f, MotionEvent.ACTION_MOVE);

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
        MotionEvent motionEvent = TestHelpers.generateMotionEvent(0f, 0f, MotionEvent.ACTION_UP);

        SignaturePathManager manager = new SignaturePathManager();

        assertEquals(0, manager.getPaths().size());

        manager.notifyTouchUp(motionEvent);

        assertEquals(1, manager.getPaths().size());
    }

    @Test
    public void testSignaturePathManagerCalculatedMinMaxBounds() {
        SignaturePathManager manager = new SignaturePathManager()
                .addAllPaths(Arrays.asList(
                        new SignaturePath(new float[]{ 0f, 0f, 10f, 100f, -25f, -5f }),
                        new SignaturePath(new float[]{ 10f, -10f, -10f, -10f }),
                        new SignaturePath(new float[]{ 100f, -100f, 10f, 100f })
                ));

        assertEquals(
                "-25.0,-100.0,100.0,100.0",
                joinToString(manager.getMinMaxBounds()));
    }

    @Test
    public void testSignaturePathManagerClearsActiveAndPreviousPaths() {
        SignaturePathManager manager = new SignaturePathManager()
                .addPath(new SignaturePath(new float[] { 100f, -100f, 10f, 100f }));

        manager.notifyTouchDown(TestHelpers.generateMotionEvent(0, 0, MotionEvent.ACTION_DOWN));

        assertEquals(1, manager.getActivePath().getCoordinateHistorySize());
        assert(manager.isSignatureInputAvailable());

        manager.clearSignaturePaths();

        assertEquals(0, manager.getActivePath().getCoordinateHistorySize());
        assertFalse(manager.isSignatureInputAvailable());
    }
}
