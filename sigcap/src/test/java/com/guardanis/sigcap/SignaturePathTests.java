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
        pathOriginal.movePathTo(new Float[] { 0f, 0f });
        pathOriginal.addPathLineTo(new Float[] { 1f, 2f });
        pathOriginal.addPathLineTo(new Float[] { 3f, 4f });

        float[] serialized = pathOriginal.serializeCoordinateHistory();

        SignaturePath pathDeserialized = new SignaturePath(serialized);

        float[] deserializedReserialized = pathDeserialized.serializeCoordinateHistory();
        String deserializedReserializedNormalized = joinToString(deserializedReserialized);

        assertEquals(serialized.length, deserializedReserialized.length);
        assertEquals(6, serialized.length);
        assertEquals("0.0,0.0,1.0,2.0,3.0,4.0", deserializedReserializedNormalized);
        assertEquals(joinToString(serialized), deserializedReserializedNormalized);
    }

    @Test
    public void testSignaturePathCloning() {
        TestSignaturePath pathOriginal = new TestSignaturePath();
        pathOriginal.movePathTo(new Float[] { 0f, 0f });
        pathOriginal.addPathLineTo(new Float[] { 1f, 2f });
        pathOriginal.addPathLineTo(new Float[] { 3f, 4f });

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
    public void testSignaturePathThrowsWhenMoveCalledMoreThanOnce() {
        TestSignaturePath path = new TestSignaturePath();
        path.movePathTo(new Float[] { 0f, 0f });
        path.movePathTo(new Float[] { 0f, 0f });

        fail("BadSignaturePathException should have been thrown");
    }

    @Test(expected = BadSignaturePathException.class)
    public void testSignaturePathThrowsWhenLineBeforeMove() {
        TestSignaturePath path = new TestSignaturePath();
        path.addPathLineTo(new Float[] { 0f, 0f });

        fail("BadSignaturePathException should have been thrown");
    }

    public static String joinToString(float[] data) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            builder.append(",");
            builder.append(String.valueOf(data[i]));
        }

        return builder.toString()
                .substring(1);
    }
}