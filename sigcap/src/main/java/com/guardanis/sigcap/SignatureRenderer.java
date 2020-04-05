package com.guardanis.sigcap;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import java.util.List;

public class SignatureRenderer implements Parcelable {

    private Paint signaturePaint = new Paint();
    private int signaturePaintColor = Color.WHITE;
    private int signatureStrokeWidth = 1;

    private Paint baselinePaint = new Paint();
    private int baselinePaintColor = Color.BLACK;
    private int baselineStrokeWidth = 1;
    private int baselinePaddingHorizontal = 12;
    private int baselinePaddingBottom = 12;

    private Paint baselineXMarkPaint = new Paint();
    private int baselineXMarkPaintColor = Color.BLACK;
    private int baselineXMarkStrokeWidth = 1;
    private int baselineXMarkLength = 12;
    private int baselineXMarkOffsetHorizontal = 12;
    private int baselineXMarkOffsetVertical = 10;

    public SignatureRenderer() {
        setupPaintDefaults();
    }

    /**
     * A {@link SignatureRenderer} styled with the default {@link Resources}
     */
    public SignatureRenderer(Resources resources) {
        this.signaturePaintColor = resources.getColor(R.color.sig__default_signature);
        this.signatureStrokeWidth = (int) resources.getDimension(R.dimen.sig__default_signature_stroke_width);

        this.baselinePaintColor = resources.getColor(R.color.sig__default_baseline);
        this.baselineStrokeWidth = (int) resources.getDimension(R.dimen.sig__default_baseline_stroke_width);
        this.baselinePaddingHorizontal = (int) resources.getDimension(R.dimen.sig__default_baseline_padding_horizontal);
        this.baselinePaddingBottom = (int) resources.getDimension(R.dimen.sig__default_baseline_padding_bottom);

        this.baselineXMarkPaintColor = resources.getColor(R.color.sig__default_baseline_x_mark);
        this.baselineXMarkStrokeWidth = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_stroke_width);
        this.baselineXMarkLength = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_length);
        this.baselineXMarkOffsetHorizontal = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_offset_horizontal);
        this.baselineXMarkOffsetVertical = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical);

        setupPaintDefaults();
    }

    /**
     * A {@link SignatureRenderer} styled with the supplied {@link TypedArray}
     *      and default {@link Resources}
     */
    public SignatureRenderer(Resources resources, TypedArray attributes) {
        this.signaturePaintColor = attributes.getColor(
                R.styleable.SignatureInputView_signatureColor,
                resources.getColor(R.color.sig__default_signature));

        this.signatureStrokeWidth = attributes.getColor(
                R.styleable.SignatureInputView_signatureStrokeWidth,
                (int) resources.getDimension(R.dimen.sig__default_signature_stroke_width));

        this.baselinePaintColor = attributes.getColor(
                R.styleable.SignatureInputView_baselineColor,
                resources.getColor(R.color.sig__default_baseline));

        this.baselineStrokeWidth = attributes.getColor(
                R.styleable.SignatureInputView_baselineStrokeWidth,
                (int) resources.getDimension(R.dimen.sig__default_baseline_stroke_width));

        this.baselinePaddingHorizontal = attributes.getInt(
                R.styleable.SignatureInputView_baselinePaddingHorizontal,
                (int) resources.getDimension(R.dimen.sig__default_baseline_padding_horizontal));

        this.baselinePaddingBottom = attributes.getInt(
                R.styleable.SignatureInputView_baselinePaddingBottom,
                (int) resources.getDimension(R.dimen.sig__default_baseline_padding_bottom));

        this.baselineXMarkPaintColor = attributes.getColor(
                R.styleable.SignatureInputView_baselineXMarkColor,
                resources.getColor(R.color.sig__default_baseline_x_mark));

        this.baselineXMarkStrokeWidth = attributes.getColor(
                R.styleable.SignatureInputView_baselineXMarkStrokeWidth,
                (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_stroke_width));

        this.baselineXMarkLength = attributes.getInt(
                R.styleable.SignatureInputView_baselineXMarkLength,
                (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_length));

        this.baselineXMarkOffsetHorizontal = attributes.getInt(
                R.styleable.SignatureInputView_baselineXMarkOffsetHorizontal,
                (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_offset_horizontal));

        this.baselineXMarkOffsetVertical = attributes.getInt(
                R.styleable.SignatureInputView_baselineXMarkOffsetVertical,
                (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical));

        setupPaintDefaults();
    }

    protected SignatureRenderer(Parcel in) {
        this.signaturePaintColor = in.readInt();
        this.signatureStrokeWidth = in.readInt();

        this.baselinePaintColor = in.readInt();
        this.baselineStrokeWidth = in.readInt();
        this.baselinePaddingHorizontal = in.readInt();
        this.baselinePaddingBottom = in.readInt();

        this.baselineXMarkPaintColor = in.readInt();
        this.baselineXMarkStrokeWidth = in.readInt();
        this.baselineXMarkLength = in.readInt();
        this.baselineXMarkOffsetHorizontal = in.readInt();
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

        baselineXMarkPaint.setAntiAlias(true);
        baselineXMarkPaint.setColor(baselineXMarkPaintColor);
        baselineXMarkPaint.setStrokeWidth(baselineXMarkStrokeWidth);
        baselineXMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        baselineXMarkPaint.setStrokeCap(Paint.Cap.ROUND);
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
        int radius = baselineXMarkLength / 2;
        int cX = baselinePaddingHorizontal + radius;
        int cY = canvas.getHeight() - baselinePaddingBottom - radius - baselineXMarkOffsetVertical;

        canvas.save();
        canvas.rotate(-45, cX, cY);
        canvas.drawLine(cX - radius, cY, cX + radius, cY, baselineXMarkPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(45, cX, cY);
        canvas.drawLine(cX - radius, cY, cX + radius, cY, baselineXMarkPaint);
        canvas.restore();
    }

    public Bitmap renderToBitmap(
            SignatureRequest request,
            SignaturePathManager manager,
            int[] renderBounds) {

        Pair<Bitmap, Canvas> result = generateResultBitmapAndCanvas(request, manager, renderBounds);

        result.second.drawColor(request.getResultBackgroundColor());

        switch (request.getResultCropStrategy()) {
            case SIGNATURE_BOUNDS:
                break;
            case CANVAS_BOUNDS:
                if (request.shouldResultIncludeBaseline()) {
                    drawBaseline(result.second);
                }

                if (request.shouldResultIncludeBaselineXMark()) {
                    drawBaselineXMark(result.second);
                }

                break;
        }

        drawPathManager(result.second, manager);

        return result.first;
    }

    private Pair<Bitmap, Canvas> generateResultBitmapAndCanvas(
            SignatureRequest request,
            SignaturePathManager manager,
            int[] renderBounds) {

        switch (request.getResultCropStrategy()) {
            case SIGNATURE_BOUNDS:
                float[] signatureMinMaxBounds = manager.getMinMaxBounds();

                Bitmap signatureSizedBitmap = Bitmap.createBitmap(
                        (int) (signatureMinMaxBounds[2] - signatureMinMaxBounds[0]),
                        (int) (signatureMinMaxBounds[3] - signatureMinMaxBounds[1]),
                        Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(signatureSizedBitmap);
                canvas.translate(-signatureMinMaxBounds[0], -signatureMinMaxBounds[1]);

                return new Pair<Bitmap, Canvas>(signatureSizedBitmap, canvas);
            case CANVAS_BOUNDS:
            default:
                Bitmap bitmap = Bitmap.createBitmap(
                        renderBounds[0],
                        renderBounds[1],
                        Bitmap.Config.ARGB_8888);

                return new Pair<Bitmap, Canvas>(bitmap, new Canvas(bitmap));
        }
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

    public SignatureRenderer setBaselineXMarkPaintColor(int baselineXMarkPaintColor) {
        this.baselineXMarkPaintColor = baselineXMarkPaintColor;
        this.baselineXMarkPaint.setColor(baselineXMarkPaintColor);

        return this;
    }

    public SignatureRenderer setBaselineXMarkStrokeWidth(int baselineXMarkStrokeWidth) {
        this.baselineXMarkStrokeWidth = baselineXMarkStrokeWidth;
        this.baselineXMarkPaint.setStrokeWidth(baselineStrokeWidth);

        return this;
    }

    public SignatureRenderer setBaselineXMarkLength(int baselineXMark) {
        this.baselineXMarkLength = baselineXMark;

        return this;
    }

    public SignatureRenderer setBaselineXMarkOffsetHorizontal(int baselineXMarkOffsetHorizontal) {
        this.baselineXMarkOffsetHorizontal = baselineXMarkOffsetHorizontal;

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

        dest.writeInt(baselineXMarkPaintColor);
        dest.writeInt(baselineXMarkStrokeWidth);
        dest.writeInt(baselineXMarkLength);
        dest.writeInt(baselineXMarkOffsetHorizontal);
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
