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
        this.resultIncludeBaseline = in.readBoolean();
        this.resultIncludeBaselineXMark = in.readBoolean();
        this.resultCropStrategy = ResultCropStrategy.valueOf(in.readString());
    }

    public int getResultBackgroundColor() {
        return resultBackgroundColor;
    }

    public SignatureRequest setResultBackgroundColor(int resultBackgroundColor) {
        this.resultBackgroundColor = resultBackgroundColor;

        return this;
    }

    public boolean shouldResultIncludeBaseline() {
        return resultIncludeBaseline;
    }

    /**
     * @param resultIncludeBaseline to show or not show te visible baseline.
     *      Value is ignored when ResultCropStrategy.SIGNATURE_BOUNDS is being used.
     */
    public SignatureRequest setResultIncludeBaseline(boolean resultIncludeBaseline) {
        this.resultIncludeBaseline = resultIncludeBaseline;

        return this;
    }

    public boolean shouldResultIncludeBaselineXMark() {
        return resultIncludeBaselineXMark;
    }

    /**
     * @param resultIncludeBaselineXMark to show or not show te baseline x-Mark.
     *      Value is ignored when ResultCropStrategy.SIGNATURE_BOUNDS is being used.
     */
    public SignatureRequest setResultIncludeBaselineXMark(boolean resultIncludeBaselineXMark) {
        this.resultIncludeBaselineXMark = resultIncludeBaselineXMark;

        return this;
    }

    public ResultCropStrategy getResultCropStrategy() {
        return resultCropStrategy;
    }

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
        dest.writeBoolean(resultIncludeBaseline);
        dest.writeBoolean(resultIncludeBaselineXMark);
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
