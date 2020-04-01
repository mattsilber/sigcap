package com.guardanis.sigcap;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class SignatureRequest implements Parcelable {

    private int resultBackgroundColor = Color.WHITE;

    private boolean resultIncludeBaseline = true;
    private boolean resultIncludeBaselineXMark = true;

    public SignatureRequest() { }

    protected SignatureRequest(Parcel in) {
        this.resultBackgroundColor = in.readInt();
        this.resultIncludeBaseline = in.readBoolean();
        this.resultIncludeBaselineXMark = in.readBoolean();
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

    public SignatureRequest setResultIncludeBaseline(boolean resultIncludeBaseline) {
        this.resultIncludeBaseline = resultIncludeBaseline;

        return this;
    }

    public boolean shouldResultIncludeBaselineXMark() {
        return resultIncludeBaselineXMark;
    }

    public SignatureRequest setResultIncludeBaselineXMark(boolean resultIncludeBaselineXMark) {
        this.resultIncludeBaselineXMark = resultIncludeBaselineXMark;

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
    }
}
