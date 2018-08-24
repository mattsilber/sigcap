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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SignatureInputView extends View implements View.OnTouchListener {

    private List<List<Path>> signaturePaths = new ArrayList<List<Path>>();
    private List<Path> activeSignaturePaths;
    private Paint signaturePaint;

    private Paint baselinePaint;
    private int baselinePaddingHorizontal = 0;
    private int baselinePaddingBottom = 0;
    private int baselineXMark = 0;
    private int baselineXMarkOffsetVertical = 0;

    private int[] lastTouchEvent;

    public SignatureInputView(Context context) {
        super(context);

        initDefaultValues();
    }

    public SignatureInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initDefaultValues();
        initAttributes(attrs, 0);
    }

    public SignatureInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefaultValues();
        initAttributes(attrs, defStyleAttr);
    }

    protected void initDefaultValues(){
        setOnTouchListener(this);

        signaturePaint = new Paint();
        signaturePaint.setAntiAlias(true);
        signaturePaint.setColor(getResources().getColor(R.color.sig__default_signature));
        signaturePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        signaturePaint.setStrokeWidth(getResources().getDimension(R.dimen.sig__default_signature_stroke));
        signaturePaint.setStrokeCap(Paint.Cap.ROUND);

        baselinePaint = new Paint();
        baselinePaint.setAntiAlias(true);
        baselinePaint.setColor(getResources().getColor(R.color.sig__default_signature));
        baselinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        baselinePaint.setStrokeWidth(getResources().getDimension(R.dimen.sig__default_baseline_height));
        baselinePaint.setStrokeCap(Paint.Cap.ROUND);

        baselinePaddingHorizontal = (int) getResources().getDimension(R.dimen.sig__default_baseline_padding_horizontal);
        baselinePaddingBottom = (int) getResources().getDimension(R.dimen.sig__default_baseline_padding_bottom);
        baselineXMark = (int) getResources().getDimension(R.dimen.sig__default_baseline_x_mark);
        baselineXMarkOffsetVertical = (int) getResources().getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical);
    }

    protected void initAttributes(AttributeSet attrs, int defStyle){
        if(attrs == null)
            return;

        TypedArray a = getContext().getTheme()
                .obtainStyledAttributes(attrs, R.styleable.SignatureInputView, defStyle, 0);

        try{
            signaturePaint.setColor(a.getColor(R.styleable.SignatureInputView_signatureColor,
                    getResources().getColor(R.color.sig__default_signature)));

            baselinePaint.setColor(a.getColor(R.styleable.SignatureInputView_baselineColor,
                    getResources().getColor(R.color.sig__default_baseline)));

            baselinePaddingHorizontal = a.getInt(R.styleable.SignatureInputView_baseline_paddingHorizontal,
                    (int) getResources().getDimension(R.dimen.sig__default_baseline_padding_horizontal));

            baselinePaddingBottom = a.getInt(R.styleable.SignatureInputView_baseline_paddingBottom,
                    (int) getResources().getDimension(R.dimen.sig__default_baseline_padding_bottom));

            baselineXMark = a.getInt(R.styleable.SignatureInputView_baseline_x_mark,
                    (int) getResources().getDimension(R.dimen.sig__default_baseline_x_mark));

            baselineXMarkOffsetVertical = a.getInt(R.styleable.SignatureInputView_baseline_x_mark_offsetVertical,
                    (int) getResources().getDimension(R.dimen.sig__default_baseline_x_mark_offset_vertical));
        }
        catch(Throwable e){ e.printStackTrace(); }
        finally { a.recycle(); }
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        int[] event = new int[]{ (int) e.getX(), (int) e.getY() };

        if(e.getAction() == MotionEvent.ACTION_UP){
            lastTouchEvent = null;

            signaturePaths.add(activeSignaturePaths);
            activeSignaturePaths = new ArrayList<Path>();
        }
        else if(e.getAction() == MotionEvent.ACTION_DOWN){
            if(!(activeSignaturePaths == null || activeSignaturePaths.size() < 1))
                signaturePaths.add(activeSignaturePaths);

            activeSignaturePaths = new ArrayList<Path>();
        }
        else{
            Path path = new Path();
            path.moveTo(lastTouchEvent[0], lastTouchEvent[1]);
            path.lineTo(event[0], event[1]);

            activeSignaturePaths.add(path);
        }

        lastTouchEvent = event;

        postInvalidate();

        return true;
    }

    public void undoLastSignaturePath(){
        if(0 < signaturePaths.size()){
            signaturePaths.remove(signaturePaths.size() - 1);

            postInvalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        drawIndicators(canvas);
        drawSignaturePaths(canvas);
    }

    protected void drawIndicators(Canvas canvas){
        canvas.drawLine(baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                canvas.getWidth() - baselinePaddingHorizontal,
                canvas.getHeight() - baselinePaddingBottom,
                baselinePaint);

        drawXMark(canvas);
    }

    protected void drawXMark(Canvas canvas){
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

    protected void drawSignaturePaths(Canvas canvas){
        for(List<Path> l : signaturePaths)
            for(Path p : l)
                canvas.drawPath(p, signaturePaint);

        if(activeSignaturePaths != null)
            for(Path p : activeSignaturePaths)
                canvas.drawPath(p, signaturePaint);
    }

    public File saveSignature() throws Exception {
        File file = new FileCache(getContext())
                .getFile(String.valueOf(System.currentTimeMillis()));

        setDrawingCacheEnabled(true);

        Bitmap bitmap = getDrawingCache();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));

        setDrawingCacheEnabled(false);

        return file;
    }

    public boolean isSignatureInputAvailable(){
        return !(signaturePaths.size() < 1 && (activeSignaturePaths == null || activeSignaturePaths.size() < 1));
    }
}
