package com.guardanis.sigcap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SignatureRenderer {

    private Paint signaturePaint = new Paint();
    private Paint baselinePaint = new Paint();

    private int baselinePaddingHorizontal = 0;
    private int baselinePaddingBottom = 0;
    private int baselineXMark = 0;
    private int baselineXMarkOffsetVertical = 0;

    public SignatureRenderer() { }

    public void drawPathGroups(Canvas canvas, @NotNull List<List<Path>> signaturePathGroups) {
        for (List<Path> pathGroup : signaturePathGroups) {
            drawPaths(canvas, pathGroup);
        }
    }

    public void drawPaths(Canvas canvas, @NotNull List<Path> signaturePaths) {
        for (Path path : signaturePaths) {
            canvas.drawPath(path, signaturePaint);
        }
    }

    public void drawBaseline(@NotNull Canvas canvas) {
        canvas.drawLine(
                baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                canvas.getWidth() - baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                baselinePaint);
    }

    public void drawBaselineXMark(@NotNull Canvas canvas) {
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
            @NotNull SignatureRequest request,
            List<List<Path>> signaturePathGroups,
            @NotNull int[] renderBounds) {

        Bitmap bitmap = Bitmap.createBitmap(renderBounds[0], renderBounds[1], Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(request.getResultBackgroundColor());

        if (request.shouldResultIncludeBaseline()) {
            drawBaseline(canvas);
        }

        if (request.shouldResultIncludeBaselineXMark()) {
            drawBaselineXMark(canvas);
        }

        drawPathGroups(canvas, signaturePathGroups);

        return bitmap;
    }

    public SignatureRenderer setSignaturePaint(Paint signaturePaint) {
        this.signaturePaint = signaturePaint;

        return this;
    }

    public SignatureRenderer setBaselinePaint(Paint baselinePaint) {
        this.baselinePaint = baselinePaint;

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
        Paint signaturePaint = createDefaultSignaturePaint(resources);
        Paint baselinePaint = createDefaultBaselinePaint(resources);

        int defaultBaselinePaddingHorizontal = (int) resources.getDimension(R.dimen.sig__default_baseline_padding_horizontal);
        int defaultBaselinePaddingBottom = (int) resources.getDimension(R.dimen.sig__default_baseline_padding_bottom);
        int defaultBaselineXMark = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark);
        int defaultBaselineXMarkOffsetVertical = (int) resources.getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical);

        return new SignatureRenderer()
                .setSignaturePaint(signaturePaint)
                .setBaselinePaint(baselinePaint)
                .setBaselinePaddingBottom(defaultBaselinePaddingBottom)
                .setBaselinePaddingHorizontal(defaultBaselinePaddingHorizontal)
                .setBaselineXMark(defaultBaselineXMark)
                .setBaselineXMarkOffsetVertical(defaultBaselineXMarkOffsetVertical);
    }

    @NotNull
    public static Paint createDefaultSignaturePaint(@NotNull Resources resources) {
        Paint signaturePaint = new Paint();
        signaturePaint.setAntiAlias(true);
        signaturePaint.setColor(resources.getColor(R.color.sig__default_signature));
        signaturePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        signaturePaint.setStrokeWidth(resources.getDimension(R.dimen.sig__default_signature_stroke));
        signaturePaint.setStrokeCap(Paint.Cap.ROUND);

        return signaturePaint;
    }

    @NotNull
    public static Paint createDefaultBaselinePaint(@NotNull Resources resources) {
        Paint baselinePaint = new Paint();
        baselinePaint.setAntiAlias(true);
        baselinePaint.setColor(resources.getColor(R.color.sig__default_signature));
        baselinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        baselinePaint.setStrokeWidth(resources.getDimension(R.dimen.sig__default_baseline_height));
        baselinePaint.setStrokeCap(Paint.Cap.ROUND);

        return baselinePaint;
    }
}
