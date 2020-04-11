package com.guardanis.sigcap;

import android.view.MotionEvent;
import android.view.View;

import com.guardanis.sigcap.paths.SignaturePathManager;

public class SignatureTouchDelegate {

    public void delegate(View view, MotionEvent event, SignaturePathManager pathManager) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                pathManager.notifyTouchUp(event);

                break;
            case MotionEvent.ACTION_DOWN:
                pathManager.notifyTouchDown(event);

                break;
            default:
                pathManager.notifyTouchMove(event);

                break;
        }
    }
}
