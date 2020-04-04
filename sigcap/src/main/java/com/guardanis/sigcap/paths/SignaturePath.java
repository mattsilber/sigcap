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

    public SignaturePath(SignaturePath original) {
        this(original.serializeCoordinateHistory());
    }

    protected SignaturePath(Parcel in) {
        this(in.createFloatArray());
    }

    public SignaturePath(float[] flattenedCoordinates) {
        if (flattenedCoordinates.length % 2 != 0)
            throw new BadSignaturePathException("SignaturePath flattened coordinate history must be an even number of digits");

        if (flattenedCoordinates.length < 2)
            return;

        movePathTo(new Float[] { flattenedCoordinates[0], flattenedCoordinates[1] });

        for (int i = 2; i < flattenedCoordinates.length; i += 2) {
            addPathLineTo(new Float[] { flattenedCoordinates[i], flattenedCoordinates[i + 1] });
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
        synchronized (path) {
            return coordinateHistory.size();
        }
    }

    /**
     * Return the minimum and maximum boundaries of this {@link SignaturePath}
     * relative to the View the coordinate history was originally
     * generated from
     *
     * @return [left, right, top, bottom] relative to the View
     *      the points were originally created from
     *
     * @throws BadSignaturePathException exception when the
     *      coordinate history is empty
     */
    public float[] getMinMaxBounds() {
        synchronized (path) {
            if (coordinateHistory.isEmpty())
                throw new BadSignaturePathException("Can't getMinMaxBounds without a history!");

            float minX = coordinateHistory.get(0)[0];
            float minY = coordinateHistory.get(0)[1];
            float maxX = coordinateHistory.get(0)[0];
            float maxY = coordinateHistory.get(0)[1];

            for (int index = 1; index < coordinateHistory.size(); index++) {
                Float[] coordinate = coordinateHistory.get(index);

                if (coordinate[0] < minX) {
                    minX = coordinate[0];
                }
                else if (maxX < coordinate[0]) {
                    maxX = coordinate[0];
                }

                if (coordinate[1] < minY) {
                    minY = coordinate[1];
                }
                else if (maxY < coordinate[1]) {
                    maxY = coordinate[1];
                }
            }

            return new float[] { minX, minY, maxX, maxY };
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(serializeCoordinateHistory());
    }

    /**
     * Flattens the coordinate history into a float array, in the order
     * in which they were added to the collection.
     *
     * @return the flattened coordinate history in the form of [x1, y1, x2, y2, ...]
     */
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
