package com.guardanis.sigcap.paths;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;

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
        this.paths.add(path);

        return this;
    }

    public SignaturePathManager addAllPaths(List<SignaturePath> paths) {
        this.paths.addAll(paths);

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
        this.paths.clear();

        return this;
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
