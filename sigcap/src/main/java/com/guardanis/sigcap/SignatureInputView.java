package com.guardanis.sigcap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignatureInputView extends View implements View.OnTouchListener {

    private List<List<Path>> signaturePaths = new ArrayList<>();
    private List<Path> activeSignaturePaths = new ArrayList<>();

    private SignatureRequest request = new SignatureRequest();
    private SignatureTouchController touchController = new SignatureTouchController();
    private SignatureRenderer renderer;

    private int[] lastRenderBounds = new int[2];

    public SignatureInputView(Context context) {
        super(context);

        setOnTouchListener(this);
        initAttributes(null, 0);
    }

    public SignatureInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnTouchListener(this);
        initAttributes(attrs, 0);
    }

    public SignatureInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnTouchListener(this);
        initAttributes(attrs, defStyleAttr);
    }

    protected void initAttributes(AttributeSet attrs, int defStyle) {
        if (attrs == null) {
            this.renderer = SignatureRenderer.createDefaultInstance(getResources());

            return;
        }

        TypedArray a = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SignatureInputView, defStyle, 0);

        try {
            this.renderer = generateSignatureRenderer(a);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }
    }

    protected SignatureRenderer generateSignatureRenderer(@NotNull TypedArray attributes) {
        Paint signaturePaint = SignatureRenderer.createDefaultSignaturePaint(getResources());
        signaturePaint.setColor(attributes.getColor(R.styleable.SignatureInputView_signatureColor,
                getResources().getColor(R.color.sig__default_signature)));

        Paint baselinePaint = SignatureRenderer.createDefaultBaselinePaint(getResources());
        baselinePaint.setColor(attributes.getColor(R.styleable.SignatureInputView_baselineColor,
                getResources().getColor(R.color.sig__default_baseline)));

        int baselinePaddingHorizontal = attributes.getInt(
                R.styleable.SignatureInputView_baseline_paddingHorizontal,
                (int) getResources().getDimension(R.dimen.sig__default_baseline_padding_horizontal));

        int baselinePaddingBottom = attributes.getInt(
                R.styleable.SignatureInputView_baseline_paddingBottom,
                (int) getResources().getDimension(R.dimen.sig__default_baseline_padding_bottom));

        int baselineXMark = attributes.getInt(
                R.styleable.SignatureInputView_baseline_x_mark,
                (int) getResources().getDimension(R.dimen.sig__default_baseline_x_mark));

        int baselineXMarkOffsetVertical = attributes.getInt(
                R.styleable.SignatureInputView_baseline_x_mark_offsetVertical,
                (int) getResources().getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical));

        return new SignatureRenderer()
                .setSignaturePaint(signaturePaint)
                .setBaselinePaint(baselinePaint)
                .setBaselinePaddingBottom(baselinePaddingBottom)
                .setBaselinePaddingHorizontal(baselinePaddingHorizontal)
                .setBaselineXMark(baselineXMark)
                .setBaselineXMarkOffsetVertical(baselineXMarkOffsetVertical);
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        touchController.handleTouchEvent(e, signaturePaths, activeSignaturePaths);

        postInvalidate();

        return true;
    }

    public void undoLastSignaturePath() {
        if (0 < signaturePaths.size()) {
            signaturePaths.remove(signaturePaths.size() - 1);

            postInvalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        renderer.drawBaseline(canvas);
        renderer.drawBaselineXMark(canvas);
        renderer.drawPathGroups(canvas, signaturePaths);
        renderer.drawPaths(canvas, activeSignaturePaths);

        this.lastRenderBounds[0] = getWidth();
        this.lastRenderBounds[1] = getHeight();
    }

    public Bitmap renderToBitmap() {
        return renderer.renderToBitmap(request, signaturePaths, lastRenderBounds);
    }

    public SignatureResponse saveSignature() {
        return new SignatureResponse(renderToBitmap());
    }

    public boolean isSignatureInputAvailable() {
        return !(signaturePaths.size() < 1 && activeSignaturePaths.size() < 1);
    }

    public void setSignatureRequest(SignatureRequest request) {
        this.request = request;
    }

    public void setSignatureRenderer(SignatureRenderer renderer) {
        this.renderer = renderer;
    }

    public SignatureRenderer getSignatureRenderer() {
        return renderer;
    }

    List<List<Path>> getSignaturePaths() {
        return signaturePaths;
    }

    void setSignaturePaths(List<List<Path>> paths) {
        signaturePaths = paths;
        postInvalidate();
    }
}
