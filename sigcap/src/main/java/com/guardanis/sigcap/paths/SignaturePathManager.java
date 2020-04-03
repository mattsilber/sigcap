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
        synchronized (paths) {
            if (1 < activePath.getCoordinateHistorySize()) {
                paths.add(activePath);
            }

            this.activePath = new SignaturePath();
            this.activePath.movePathTo(event);
        }
    }

    public void notifyTouchMove(MotionEvent event) {
        synchronized (paths) {
            this.activePath.addPathLineTo(event);
        }
    }

    public void notifyTouchUp(MotionEvent event) {
        synchronized (paths) {
            paths.add(activePath);

            this.activePath = new SignaturePath();
        }
    }

    public void undoLastPath() {
        synchronized (paths) {
            if (paths.size() < 1)
                return;

            paths.remove(paths.size() - 1);
        }
    }

    public SignaturePathManager addPath(SignaturePath path) {
        synchronized (paths) {
            this.paths.add(path);
        }

        return this;
    }

    public SignaturePathManager addAllPaths(List<SignaturePath> paths) {
        synchronized (paths) {
            this.paths.addAll(paths);
        }

        return this;
    }

    public List<SignaturePath> getPaths() {
        return paths;
    }

    public SignaturePath getActivePath() {
        return activePath;
    }

    public boolean isSignatureInputAvailable() {
        return 0 < paths.size();
    }

    public SignaturePathManager clearSignaturePaths() {
        synchronized (paths) {
            this.paths.clear();
        }

        return this;
    }

    public float[] getMinMaxBounds() {
        synchronized (paths) {
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
