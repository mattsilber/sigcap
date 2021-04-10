package com.guardanis.sigcap.paths;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

import com.guardanis.sigcap.exceptions.BadSignaturePathException;

import java.util.ArrayList;
import java.util.List;

public class SignaturePathManager implements Parcelable {

    protected final List<SignaturePath> paths;
    protected SignaturePath activePath = new SignaturePath();

    public SignaturePathManager() {
        this.paths = new ArrayList<SignaturePath>();
    }

    protected SignaturePathManager(Parcel in) {
        this.paths = in.createTypedArrayList(SignaturePath.CREATOR);
        this.activePath = in.readParcelable(SignaturePath.class.getClassLoader());
    }

    public void notifyTouchDown(MotionEvent event) {
        synchronized (this.paths) {
            if (1 < activePath.getCoordinateHistorySize()) {
                paths.add(activePath);
            }

            this.activePath = createSignaturePathInstance();
            this.activePath.movePathTo(event);
        }
    }

    public void notifyTouchMove(MotionEvent event) {
        synchronized (this.paths) {
            this.activePath.addPathLineTo(event);
        }
    }

    public void notifyTouchUp(MotionEvent event) {
        synchronized (this.paths) {
            paths.add(activePath);

            this.activePath = createSignaturePathInstance();
        }
    }

    protected SignaturePath createSignaturePathInstance() {
        return new SignaturePath();
    }

    public void undoLastPath() {
        synchronized (this.paths) {
            if (paths.size() < 1)
                return;

            paths.remove(paths.size() - 1);
        }
    }

    /**
     * Append the supplied {@link SignaturePath} instance to
     * the end of the path history.
     */
    public SignaturePathManager addPath(SignaturePath path) {
        synchronized (this.paths) {
            this.paths.add(path);
        }

        return this;
    }

    /**
     * Append all of the supplied {@link SignaturePath} instances to
     * the end of the path history.
     */
    public SignaturePathManager addAllPaths(List<SignaturePath> paths) {
        synchronized (this.paths) {
            this.paths.addAll(paths);
        }

        return this;
    }

    /**
     * @return a clone of the collection of {@link SignaturePath} instances
     *      that this {@link SignaturePathManager} has collected through
     *      various inputs
     */
    public List<SignaturePath> getClonedPaths() {
        synchronized (this.paths) {
            ArrayList<SignaturePath> cloned = new ArrayList<SignaturePath>();

            for (SignaturePath original : this.paths)
                cloned.add(new SignaturePath(original));

            return cloned;
        }
    }

    /**
     * @return the actual collection of {@link SignaturePath} instances
     *      that this {@link SignaturePathManager} has collected through
     *      various inputs
     */
    public List<SignaturePath> getPaths() {
        synchronized (this.paths) {
            return this.paths;
        }
    }

    /**
     * @return a clone of the {@link SignaturePath} instance
     *      that this {@link SignaturePathManager} is actively passing
     *      various inputs to
     */
    public SignaturePath getClonedActivePath() {
        synchronized (this.paths) {
            return new SignaturePath(activePath);
        }
    }

    /**
     * @return the actual {@link SignaturePath} instance
     *      that this {@link SignaturePathManager} is actively passing
     *      various inputs to
     */
    public SignaturePath getActivePath() {
        synchronized (this.paths) {
            return this.activePath;
        }
    }

    /**
     * @return true if at least one complete {@link SignaturePath} exists
     */
    public boolean isSignatureInputAvailable() {
        synchronized (this.paths) {
            return 0 < paths.size();
        }
    }

    /**
     * Remove all {@link SignaturePath} instances
     */
    public SignaturePathManager clearSignaturePaths() {
        synchronized (this.paths) {
            this.paths.clear();
        }

        return this;
    }

    /**
     * Return the minimum and maximum boundaries of all {@link SignaturePath}
     * instances collected by this {@link SignaturePathManager}
     *
     * @return [left, right, top, bottom] relative to the View
     *      the points were originally created from
     *
     * @throws BadSignaturePathException exception when no complete
     *      {@link SignaturePath} instances have been collected
     */
    public float[] getMinMaxBounds() {
        synchronized (this.paths) {
            if (paths.isEmpty())
                throw new BadSignaturePathException("Can't getMinMaxBounds without a history!");

            float[] firstPathBounds = paths.get(0)
                    .getMinMaxBounds();

            float minX = firstPathBounds[0];
            float minY = firstPathBounds[1];
            float maxX = firstPathBounds[2];
            float maxY = firstPathBounds[3];

            for (int index = 1; index < paths.size(); index++) {
                float[] bounds = paths.get(index)
                        .getMinMaxBounds();

                if (bounds[0] < minX) {
                    minX = bounds[0];
                }

                if (bounds[1] < minY) {
                    minY = bounds[1];
                }

                if (maxX < bounds[2]) {
                    maxX = bounds[2];
                }

                if (maxY < bounds[3]) {
                    maxY = bounds[3];
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
        dest.writeTypedList(paths);
        dest.writeParcelable(activePath, flags);
    }

    public static final Creator<SignaturePathManager> CREATOR = new Creator<SignaturePathManager>() {

        @Override
        public SignaturePathManager createFromParcel(Parcel in) {
            return new SignaturePathManager(in);
        }

        @Override
        public SignaturePathManager[] newArray(int size) {
            return new SignaturePathManager[size];
        }
    };
}
