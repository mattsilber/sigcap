package com.guardanis.sigcap;

import com.guardanis.sigcap.paths.SignaturePath;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    private String joinToString(float[] data) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            builder.append(",");
            builder.append(String.valueOf(data[i]));
        }

        return builder.toString()
                .substring(1);
    }
}