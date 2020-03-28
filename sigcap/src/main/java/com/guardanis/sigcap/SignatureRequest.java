package com.guardanis.sigcap;

import android.content.Context;
import android.graphics.Color;

public class SignatureRequest {

    private int resultBackgroundColor = Color.WHITE;

    private boolean includeBaseline = true;
    private boolean includeBaselineXMark = true;

    public int getResultBackgroundColor() {
        return resultBackgroundColor;
    }

    public SignatureRequest setResultBackgroundColor(int resultBackgroundColor) {
        this.resultBackgroundColor = resultBackgroundColor;

        return this;
    }

    public boolean shouldIncludeBaseline() {
        return includeBaseline;
    }

    public SignatureRequest setIncludeBaseline(boolean includeBaseline) {
        this.includeBaseline = includeBaseline;

        return this;
    }

    public boolean shouldIncludeBaselineXMark() {
        return includeBaselineXMark;
    }

    public SignatureRequest setIncludeBaselineXMark(boolean includeBaselineXMark) {
        this.includeBaselineXMark = includeBaselineXMark;

        return this;
    }
}
