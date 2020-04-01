package com.guardanis.sigcap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import java.util.List;

public class SignatureRenderer implements Parcelable {

    private Paint signaturePaint = new Paint();
    private int signaturePaintColor = Color.WHITE;
    private int signatureStrokeWidth = 1;

    private Paint baselinePaint = new Paint();
    private int baselinePaintColor = Color.WHITE;
    private int baselineStrokeWidth = 1;

    private int baselinePaddingHorizontal = 0;
    private int baselinePaddingBottom = 0;
    private int baselineXMark = 0;
    private int baselineXMarkOffsetVertical = 0;

    public SignatureRenderer() {
        setupPaintDefaults();
    }

    public SignatureRenderer(Resources resources) {
        this.baselinePaddingHorizontal = (int) resources.getDimension(R.dimen.sig__default_baseline_padding_horizontal);
        this.baselinePaddingBottom = (int) resources.getDimension(R.dimen.sig__default_baseline_padding_bottom);
        this.baselineXMark = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark);
        this.baselineXMarkOffsetVertical = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical);

        this.signaturePaintColor = resources.getColor(R.color.sig__default_signature);
        this.signatureStrokeWidth = (int) resources.getDimension(R.dimen.sig__default_signature_stroke);

        this.baselinePaintColor = resources.getColor(R.color.sig__default_baseline);
        this.baselineStrokeWidth = (int) resources.getDimension(R.dimen.sig__default_baseline_height);

        setupPaintDefaults();
    }

    protected SignatureRenderer(Parcel in) {
        this.signaturePaintColor = in.readInt();
        this.signatureStrokeWidth = in.readInt();
        this.baselinePaintColor = in.readInt();
        this.baselineStrokeWidth = in.readInt();
        this.baselinePaddingHorizontal = in.readInt();
        this.baselinePaddingBottom = in.readInt();
        this.baselineXMark = in.readInt();
        this.baselineXMarkOffsetVertical = in.readInt();

        setupPaintDefaults();
    }

    protected void setupPaintDefaults() {
        signaturePaint.setAntiAlias(true);
        signaturePaint.setColor(signaturePaintColor);
        signaturePaint.setStrokeWidth(signatureStrokeWidth);
        signaturePaint.setStyle(Paint.Style.STROKE);
        signaturePaint.setStrokeCap(Paint.Cap.ROUND);

        baselinePaint.setAntiAlias(true);
        baselinePaint.setColor(baselinePaintColor);
        baselinePaint.setStrokeWidth(baselineStrokeWidth);
        baselinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        baselinePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void drawPathManager(Canvas canvas, SignaturePathManager manager) {
        drawPaths(canvas, manager.getPaths());

        canvas.drawPath(manager.getActivePath().getPath(), signaturePaint);
    }

    public void drawPaths(Canvas canvas, List<SignaturePath> paths) {
        for (SignaturePath signaturePath : paths) {
            canvas.drawPath(signaturePath.getPath(), signaturePaint);
        }
    }

    public void drawBaseline(Canvas canvas) {
        canvas.drawLine(
                baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                canvas.getWidth() - baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                baselinePaint);
    }

    public void drawBaselineXMark(Canvas canvas) {
        int radius = baselineXMark / 2;
        int cX = baselinePaddingHorizontal + radius;
        int cY = canvas.getHeight() - baselinePaddingBottom - radius - baselineXMarkOffsetVertical;

        canvas.save();
        canvas.rotate(-45, cX, cY);
        canvas.drawLine(cX - radius, cY, cX + radius, cY, baselinePaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(45, cX, cY);
        canvas.drawLine(cX - radius, cY, cX + radius, cY, baselinePaint);
        canvas.restore();
    }

    public Bitmap renderToBitmap(
            SignatureRequest request,
            SignaturePathManager manager,
            int[] renderBounds) {

        Bitmap bitmap = Bitmap.createBitmap(renderBounds[0], renderBounds[1], Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(request.getResultBackgroundColor());

        if (request.shouldResultIncludeBaseline()) {
            drawBaseline(canvas);
        }

        if (request.shouldResultIncludeBaselineXMark()) {
            drawBaselineXMark(canvas);
        }

        drawPathManager(canvas, manager);

        return bitmap;
    }

    public SignatureRenderer setSignaturePaintColor(int signaturePaintColor) {
        this.signaturePaintColor = signaturePaintColor;
        this.signaturePaint.setColor(signaturePaintColor);

        return this;
    }

    public SignatureRenderer setSignatureStrokeWidth(int signatureStrokeWidth) {
        this.signatureStrokeWidth = signatureStrokeWidth;
        this.signaturePaint.setStrokeWidth(signatureStrokeWidth);

        return this;
    }

    public SignatureRenderer setBaselinePaintColor(int baselinePaintColor) {
        this.baselinePaintColor = baselinePaintColor;
        this.baselinePaint.setColor(baselinePaintColor);

        return this;
    }

    public SignatureRenderer setBaselineStrokeWidth(int baselineStrokeWidth) {
        this.baselineStrokeWidth = baselineStrokeWidth;
        this.baselinePaint.setStrokeWidth(baselineStrokeWidth);

        return this;
    }

    public SignatureRenderer setBaselinePaddingHorizontal(int baselinePaddingHorizontal) {
        this.baselinePaddingHorizontal = baselinePaddingHorizontal;

        return this;
    }

    public SignatureRenderer setBaselinePaddingBottom(int baselinePaddingBottom) {
        this.baselinePaddingBottom = baselinePaddingBottom;

        return this;
    }

    public SignatureRenderer setBaselineXMark(int baselineXMark) {
        this.baselineXMark = baselineXMark;

        return this;
    }

    public SignatureRenderer setBaselineXMarkOffsetVertical(int baselineXMarkOffsetVertical) {
        this.baselineXMarkOffsetVertical = baselineXMarkOffsetVertical;

        return this;
    }

    public static SignatureRenderer createDefaultInstance(Resources resources) {
        return new SignatureRenderer(resources);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(signaturePaintColor);
        dest.writeInt(signatureStrokeWidth);
        dest.writeInt(baselinePaintColor);
        dest.writeInt(baselineStrokeWidth);
        dest.writeInt(baselinePaddingHorizontal);
        dest.writeInt(baselinePaddingBottom);
        dest.writeInt(baselineXMark);
        dest.writeInt(baselineXMarkOffsetVertical);
    }

    public static final Creator<SignatureRenderer> CREATOR = new Creator<SignatureRenderer>() {

        @Override
        public SignatureRenderer createFromParcel(Parcel in) {
            return new SignatureRenderer(in);
        }

        @Override
        public SignatureRenderer[] newArray(int size) {
            return new SignatureRenderer[size];
        }
    };
}
