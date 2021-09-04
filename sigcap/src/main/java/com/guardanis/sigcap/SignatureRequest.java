package com.guardanis.sigcap;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class SignatureRequest implements Parcelable {

    public enum ResultCropStrategy {
        CANVAS_BOUNDS,
        SIGNATURE_BOUNDS
    }

    private int resultBackgroundColor = Color.WHITE;

    private boolean resultIncludeBaseline = true;
    private boolean resultIncludeBaselineXMark = true;

    private ResultCropStrategy resultCropStrategy = ResultCropStrategy.CANVAS_BOUNDS;

    public SignatureRequest() { }

    protected SignatureRequest(Parcel in) {
        this.resultBackgroundColor = in.readInt();
        this.resultIncludeBaseline = in.readInt() == 1;
        this.resultIncludeBaselineXMark = in.readInt() == 1;
        this.resultCropStrategy = ResultCropStrategy.valueOf(in.readString());
    }

    public int getResultBackgroundColor() {
        return resultBackgroundColor;
    }

    /**
     * Set a background color for the {@link android.graphics.Bitmap} that will
     * be generated upon a successful submission of a signature.
     *
     * This will not affect the active rendering of the {@link SignatureInputView}
     */
    public SignatureRequest setResultBackgroundColor(int resultBackgroundColor) {
        this.resultBackgroundColor = resultBackgroundColor;

        return this;
    }

    public boolean shouldResultIncludeBaseline() {
        return resultIncludeBaseline;
    }

    /**
     * Set whether or not to draw the bottom baseline for the
     * {@link android.graphics.Bitmap} that will be generated upon a successful
     * submission of a signature.
     *
     * This will not affect the active rendering of the {@link SignatureInputView}
     *
     * Note: Value is ignored when ResultCropStrategy.SIGNATURE_BOUNDS is being used
     */
    public SignatureRequest setResultIncludeBaseline(boolean resultIncludeBaseline) {
        this.resultIncludeBaseline = resultIncludeBaseline;

        return this;
    }

    public boolean shouldResultIncludeBaselineXMark() {
        return resultIncludeBaselineXMark;
    }

    /**
     * Set whether or not to draw the bottom baseline x-mark for the
     * {@link android.graphics.Bitmap} that will be generated upon a successful
     * submission of a signature.
     *
     * This will not affect the active rendering of the {@link SignatureInputView}
     *
     * Note: Value is ignored when ResultCropStrategy.SIGNATURE_BOUNDS is being used
     */
    public SignatureRequest setResultIncludeBaselineXMark(boolean resultIncludeBaselineXMark) {
        this.resultIncludeBaselineXMark = resultIncludeBaselineXMark;

        return this;
    }

    public ResultCropStrategy getResultCropStrategy() {
        return resultCropStrategy;
    }

    /**
     * Set the strategy for rendering the signature to the {@link android.graphics.Bitmap}
     * that will be generated upon a successful submission of a signature.
     *
     * CANVAS_BOUNDS, the default, will render the signature to a {@link android.graphics.Bitmap}
     * generated from same boundaries as the canvas in which the signature was created from.
     *
     * SIGNATURE_BOUNDS will create a {@link android.graphics.Bitmap} from the minimum
     * and maximum boundaries of all collected {@link com.guardanis.sigcap.paths.SignaturePath}
     * instances, so that no visual cropping occurs in the result. This mode cannot be used with
     * baseline or baseline-x-mark drawing (for now).
     *
     * This will not affect the active rendering of the {@link SignatureInputView}
     */
    public SignatureRequest setResultCropStrategy(ResultCropStrategy resultCropStrategy) {
        this.resultCropStrategy = resultCropStrategy;

        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resultBackgroundColor);
        dest.writeInt(resultIncludeBaseline ? 1 : 0);
        dest.writeInt(resultIncludeBaselineXMark ? 1 : 0);
        dest.writeString(resultCropStrategy.name());
    }

    public static final Creator<SignatureRequest> CREATOR = new Creator<SignatureRequest>() {

        @Override
        public SignatureRequest createFromParcel(Parcel in) {
            return new SignatureRequest(in);
        }

        @Override
        public SignatureRequest[] newArray(int size) {
            return new SignatureRequest[size];
        }
    };
}
