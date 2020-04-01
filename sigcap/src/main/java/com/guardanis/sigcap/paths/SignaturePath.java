package com.guardanis.sigcap.paths;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class SignaturePath implements Parcelable {

    private Path path = new Path();
    private List<Float[]> coordinateHistory = new ArrayList<Float[]>();

    public SignaturePath() { }

    protected SignaturePath(Parcel in) {
        float[] stored = in.createFloatArray();

        if (stored.length < 2)
            return;

        movePathTo(new Float[] { stored[0], stored[1] });

        for (int i = 2; i < stored.length; i += 2) {
            addPathLineTo(new Float[] { stored[i], stored[i + 1] });
        }
    }

    public void movePathTo(MotionEvent event) {
        movePathTo(new Float[] { event.getX(), event.getY() });
    }

    private void movePathTo(Float[] coordinates) {
        synchronized (path) {
            this.path.moveTo(coordinates[0], coordinates[1]);
            this.coordinateHistory.add(coordinates);
        }
    }

    public void addPathLineTo(MotionEvent event) {
        addPathLineTo(new Float[] { event.getX(), event.getY() });
    }

    private void addPathLineTo(Float[] coordinates) {
        synchronized (path) {
            this.path.lineTo(coordinates[0], coordinates[1]);
            this.coordinateHistory.add(coordinates);
        }
    }

    public Path getPath() {
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        float[] flattened = new float[coordinateHistory.size() * 2];

        for (int index = 0; index < flattened.length; index += 2) {
            Float[] coordinate = coordinateHistory.get(index);

            flattened[index] = coordinate[0];
            flattened[index + 1] = coordinate[1];
        }

        dest.writeFloatArray(flattened);
    }

    public static final Creator<SignaturePath> CREATOR = new Creator<SignaturePath>() {

        @Override
        public SignaturePath createFromParcel(Parcel in) {
            return new SignaturePath(in);
        }

        @Override
        public SignaturePath[] newArray(int size) {
            return new SignaturePath[size];
        }
    };
}
