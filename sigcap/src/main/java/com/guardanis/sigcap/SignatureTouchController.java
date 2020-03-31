package com.guardanis.sigcap;

import android.graphics.Path;
import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class SignatureTouchController {

    private float[] lastTouchEvent;

    void handleTouchEvent(
            @NotNull MotionEvent event,
            List<List<Path>> signaturePaths,
            List<Path> activeSignaturePaths) {

        float[] currentTouchEvent = new float[2];
        currentTouchEvent[0] = event.getX();
        currentTouchEvent[1] = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                this.lastTouchEvent = null;

                signaturePaths.add(new ArrayList<>(activeSignaturePaths));
                activeSignaturePaths.clear();

                break;
            case MotionEvent.ACTION_DOWN:
                if(0 < activeSignaturePaths.size())
                    signaturePaths.add(new ArrayList<>(activeSignaturePaths));

                activeSignaturePaths.clear();

                break;
            default:
                Path path = new Path();
                path.moveTo(lastTouchEvent[0], lastTouchEvent[1]);
                path.lineTo(currentTouchEvent[0], currentTouchEvent[1]);

                activeSignaturePaths.add(path);

                break;
        }

        this.lastTouchEvent = currentTouchEvent;
    }
}
