package com.guardanis.sigcap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.guardanis.sigcap.paths.SignaturePath;
import com.guardanis.sigcap.paths.SignaturePathManager;

import java.util.List;

public class SignatureInputView extends View implements View.OnTouchListener {

    public static final String TAG = "signcap";

    public static final String KEY__SIGNATURE_REQUEST = "sdf__request";
    public static final String KEY__SIGNATURE_RENDERER = "sdf__renderer";
    public static final String KEY__SIGNATURE_PATH_MANAGER = "sdf__path_manager";

    private SignatureRequest request = new SignatureRequest();
    private SignaturePathManager pathManager = new SignaturePathManager();
    private SignatureTouchDelegate touchDelegate = new SignatureTouchDelegate();
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
        }
        catch (Throwable e) { e.printStackTrace(); }
        finally { a.recycle(); }
    }

    protected SignatureRenderer generateSignatureRenderer(TypedArray attributes) {
        int signatureColor = attributes.getColor(
                R.styleable.SignatureInputView_signatureColor,
                getResources().getColor(R.color.sig__default_signature));

        int baselineColor = attributes.getColor(
                R.styleable.SignatureInputView_baselineColor,
                getResources().getColor(R.color.sig__default_baseline));

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

        return new SignatureRenderer(getResources())
                .setSignaturePaintColor(signatureColor)
                .setBaselinePaintColor(baselineColor)
                .setBaselinePaddingBottom(baselinePaddingBottom)
                .setBaselinePaddingHorizontal(baselinePaddingHorizontal)
                .setBaselineXMark(baselineXMark)
                .setBaselineXMarkOffsetVertical(baselineXMarkOffsetVertical);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        touchDelegate.delegate(view, event, pathManager);

        postInvalidate();

        return true;
    }

    public void undoLastSignaturePath() {
        pathManager.undoLastPath();

        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        renderer.drawBaseline(canvas);
        renderer.drawBaselineXMark(canvas);
        renderer.drawPathManager(canvas, pathManager);

        this.lastRenderBounds[0] = getWidth();
        this.lastRenderBounds[1] = getHeight();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SignatureState state = new SignatureState(super.onSaveInstanceState());
        state.signatureData.putParcelable(KEY__SIGNATURE_REQUEST, request);
        state.signatureData.putParcelable(KEY__SIGNATURE_RENDERER, renderer);
        state.signatureData.putParcelable(KEY__SIGNATURE_PATH_MANAGER, pathManager);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SignatureState)) {
            super.onRestoreInstanceState(state);

            return;
        }

        SignatureState signatureState = (SignatureState) state;

        this.request = signatureState.signatureData.getParcelable(KEY__SIGNATURE_REQUEST);
        this.renderer = signatureState.signatureData.getParcelable(KEY__SIGNATURE_RENDERER);
        this.pathManager = signatureState.signatureData.getParcelable(KEY__SIGNATURE_PATH_MANAGER);

        super.onRestoreInstanceState(signatureState.getSuperState());
    }

    public Bitmap renderToBitmap() {
        return renderer.renderToBitmap(request, pathManager, lastRenderBounds);
    }

    public SignatureResponse saveSignature() {
        return new SignatureResponse(renderToBitmap());
    }

    public boolean isSignatureInputAvailable() {
        return pathManager.isSignatureInputAvailable();
    }

    public void setSignatureRequest(SignatureRequest request) {
        this.request = request;
    }

    public SignatureRequest getSignatureRequest() {
        return request;
    }

    public void setSignatureRenderer(SignatureRenderer renderer) {
        this.renderer = renderer;
        this.postInvalidate();
    }

    public SignatureRenderer getSignatureRenderer() {
        return renderer;
    }

    public SignaturePathManager getPathManager() {
        return pathManager;
    }

    public void setPathManager(SignaturePathManager pathManager) {
        this.pathManager = pathManager;
        this.postInvalidate();
    }

    /**
     * Get signature paths.
     * @author Yordan P. Dieguez
     */
    public List<SignaturePath> getSignaturePaths() {
        return pathManager.getPaths();
    }

    protected static class SignatureState extends BaseSavedState {

        Bundle signatureData = new Bundle();

        SignatureState(Parcelable state) {
            super(state);
        }

        SignatureState(Parcel in) {
            super(in);

            this.signatureData = in.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeBundle(this.signatureData);
        }

        public static final Parcelable.Creator<SignatureState> CREATOR = new Parcelable.Creator<SignatureState>() {

            @Override
            public SignatureState createFromParcel(Parcel in) {
                return new SignatureState(in);
            }

            @Override
            public SignatureState[] newArray(int size) {
                return new SignatureState[size];
            }
        };
    }
}
