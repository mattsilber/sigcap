package com.guardanis.sigcap;

import android.graphics.Color;

public class SignatureRequest {

    private int resultBackgroundColor = Color.WHITE;

    private boolean resultIncludeBaseline = true;
    private boolean resultIncludeBaselineXMark = true;

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
}
