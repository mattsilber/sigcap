package com.guardanis.sigcap.paths;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

import com.guardanis.sigcap.exceptions.BadSignaturePathException;

import java.util.ArrayList;
import java.util.List;

public class SignaturePath implements Parcelable {

    private Path path = new Path();
    private List<Float[]> coordinateHistory = new ArrayList<Float[]>();

    public SignaturePath() { }

    protected SignaturePath(Parcel in) {
        this(in.createFloatArray());
    }

    public SignaturePath(float[] flattenedCoordinates) {
        if (flattenedCoordinates.length < 2)
            return;

        try {
            movePathTo(new Float[] { flattenedCoordinates[0], flattenedCoordinates[1] });

            for (int i = 2; i < flattenedCoordinates.length; i += 2) {
                addPathLineTo(new Float[] { flattenedCoordinates[i], flattenedCoordinates[i + 1] });
            }
        }
        catch (Exception e) {
            throw new BadSignaturePathException(e);
        }
    }

    public void movePathTo(MotionEvent event) {
        movePathTo(new Float[] { event.getX(), event.getY() });
    }

    protected void movePathTo(Float[] coordinates) {
        synchronized (path) {
            if (!coordinateHistory.isEmpty())
                throw new BadSignaturePathException("movePathTo should only be called once per SignaturePath instance");

            this.path.moveTo(coordinates[0], coordinates[1]);
            this.coordinateHistory.add(coordinates);
        }
    }

    public void addPathLineTo(MotionEvent event) {
        addPathLineTo(new Float[] { event.getX(), event.getY() });
    }

    protected void addPathLineTo(Float[] coordinates) {
        synchronized (path) {
            if (coordinateHistory.isEmpty())
                throw new BadSignaturePathException("movePathTo must be called before addPathLineTo");

            this.path.lineTo(coordinates[0], coordinates[1]);
            this.coordinateHistory.add(coordinates);
        }
    }

    public Path getPath() {
        return path;
    }

    public int getCoordinateHistorySize() {
        return coordinateHistory.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(serializeCoordinateHistory());
    }

    public float[] serializeCoordinateHistory() {
        synchronized (path) {
            int coordinateHistorySize = coordinateHistory.size();
            int flattenedIndex = 0;

            float[] flattened = new float[coordinateHistorySize * 2];

            for (int historyIndex = 0; historyIndex < coordinateHistorySize; historyIndex += 1) {
                Float[] coordinate = coordinateHistory.get(historyIndex);

                flattened[flattenedIndex] = coordinate[0];
                flattened[flattenedIndex + 1] = coordinate[1];

                flattenedIndex += 2;
            }

            return flattened;
        }
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
