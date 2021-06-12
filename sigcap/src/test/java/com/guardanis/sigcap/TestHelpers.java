package com.guardanis.sigcap;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class TestHelpers {

    static <T extends Parcelable> T parcelizeAndRecreate(T element, Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();

        element.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        return creator.createFromParcel(parcel);
    }

    public static MotionEvent generateMotionEvent(float x, float y, int action) {
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
