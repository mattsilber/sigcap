package com.guardanis.sigcap;

import com.guardanis.sigcap.exceptions.BadSignaturePathException;
import com.guardanis.sigcap.paths.SignaturePath;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SignaturePathTests {

    @Test
    public void testSignaturePathSerialization() {
        TestSignaturePath pathOriginal = new TestSignaturePath();
        pathOriginal.startPathAt(0f, 0f);
        pathOriginal.addPathLineTo(1f, 2f);
        pathOriginal.addPathLineTo(3f, 4f);

        float[] serialized = pathOriginal.serializeCoordinateHistory();

        SignaturePath pathDeserialized = new SignaturePath(serialized);

        float[] deserializedReserialized = pathDeserialized.serializeCoordinateHistory();
        String deserializedReserializedNormalized = joinToString(deserializedReserialized);

        String expected = "0.0,0.0,1.0,2.0,3.0,4.0";

        assertEquals(serialized.length, deserializedReserialized.length);
        assertEquals(6, serialized.length);
        assertEquals(expected, deserializedReserializedNormalized);
        assertEquals(joinToString(serialized), deserializedReserializedNormalized);

        SignaturePath pathFromParcel = TestHelpers.parcelizeAndRecreate(pathDeserialized, SignaturePath.CREATOR);

        deserializedReserialized = pathFromParcel.serializeCoordinateHistory();
        deserializedReserializedNormalized = joinToString(deserializedReserialized);

        assertEquals(serialized.length, deserializedReserialized.length);
        assertEquals(expected, deserializedReserializedNormalized);
    }

    @Test
    public void testSignaturePathCloning() {
        TestSignaturePath pathOriginal = new TestSignaturePath();
        pathOriginal.startPathAt(0f, 0f);
        pathOriginal.addPathLineTo(1f, 2f);
        pathOriginal.addPathLineTo(3f, 4f);

        SignaturePath cloned = new SignaturePath(pathOriginal);

        assertEquals(
                joinToString(pathOriginal.serializeCoordinateHistory()),
                joinToString(cloned.serializeCoordinateHistory()));
    }

    @Test
    public void testSignaturePathMinMaxBounds() {
        assertEquals(
                "0.0,0.0,10.0,10.0",
                joinToString(
                        new SignaturePath(new float[] { 0f, 0f, 10f, 10f })
                                .getMinMaxBounds()));
        assertEquals(
                "-10.0,-10.0,100.0,100.0",
                joinToString(
                        new SignaturePath(new float[] { -10f, -10f, 10f, 10f , 100f, 100f, -5f, -5f })
                                .getMinMaxBounds()));
        assertEquals(
                "10.0,-100.0,100.0,100.0",
                joinToString(
                        new SignaturePath(new float[] { 100f, -100f, 10f, 100f })
                                .getMinMaxBounds()));
    }

    @Test(expected = BadSignaturePathException.class)
    public void testSignaturePathThrowsWhenDeserializationFails() {
        new SignaturePath(new float[] { 0f, 0f, 0f });

        fail("BadSignaturePathException should have been thrown");
    }

    @Test
    public void testSignaturePathDeserializesPathWithSingleMoveEvent() {
        String result = joinToString(
                new SignaturePath(new float[] { 1f, 1f })
                        .serializeCoordinateHistory());

        assertEquals("1.0,1.0", result);
    }

    @Test(expected = BadSignaturePathException.class)
    public void testSignaturePathThrowsWhenStartPathAtCalledMoreThanOnce() {
        TestSignaturePath path = new TestSignaturePath();
        path.startPathAt(0f, 0f);
        path.startPathAt(0f, 0f);

        fail("BadSignaturePathException should have been thrown");
    }

    @Test(expected = BadSignaturePathException.class)
    public void testSignaturePathThrowsWhenLineBeforeMove() {
        TestSignaturePath path = new TestSignaturePath();
        path.addPathLineTo(0f, 0f);

        fail("BadSignaturePathException should have been thrown");
    }

    @Test(expected = BadSignaturePathException.class)
    public void testSignaturePathThrowsGetMinMaxBoundsWithNoCoordinateHistory() {
        TestSignaturePath path = new TestSignaturePath();
        path.getMinMaxBounds();

        fail("BadSignaturePathException should have been thrown");
    }

    public static String joinToString(float[] data) {
        StringBuilder builder = new StringBuilder();

        for (float datum : data) {
            builder.append(",");
            builder.append(datum);
        }

        return builder.toString()
                .substring(1);
    }
}