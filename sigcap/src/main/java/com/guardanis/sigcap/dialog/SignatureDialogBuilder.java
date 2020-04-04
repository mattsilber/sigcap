package com.guardanis.sigcap.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.guardanis.sigcap.exceptions.NoSignatureException;
import com.guardanis.sigcap.R;
import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.SignatureInputView;
import com.guardanis.sigcap.SignatureRenderer;
import com.guardanis.sigcap.SignatureRequest;
import com.guardanis.sigcap.SignatureResponse;
import com.guardanis.sigcap.paths.SignaturePathManager;

import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_PATH_MANAGER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_RENDERER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_REQUEST;
import static com.guardanis.sigcap.dialog.SignatureDialogFragment.KEY__AUTO_ATTACH_EVENT_LISTENER;

/**
 * Helper class to show dialog.
 * To use it, the activity or parent fragment must implement {@link SignatureEventListener} interface,
 * otherwise it will throw an {@link RuntimeException}
 *
 * @author Matt Silber
 * @author Yordan P. Dieguez
 */
public class SignatureDialogBuilder {

    private SignatureRequest request = new SignatureRequest();
    private SignaturePathManager pathManager;
    private SignatureRenderer renderer;

    private boolean autoAttachEventListener;

    /**
     * Pass an {@link SignatureRequest} object before show the dialog.
     *
     * @param request {@link SignatureRequest} object
     * @return {@link SignatureDialogBuilder}
     * @author Matt Silber
     */
    public SignatureDialogBuilder setRequest(SignatureRequest request) {
        this.request = request;

        return this;
    }

    public SignatureRequest getRequest() {
        return request;
    }

    /**
     * Pass an {@link SignatureRenderer} object before show the dialog.
     *
     * @param renderer {@link SignatureRenderer} object
     * @return {@link SignatureDialogBuilder}
     *
     *  @author Matt Silber
     */
    public SignatureDialogBuilder setRenderer(SignatureRenderer renderer) {
        this.renderer = renderer;

        return this;
    }

    public SignatureRenderer getRenderer() {
        return renderer;
    }

    public SignatureDialogBuilder setPathManager(SignaturePathManager pathManager) {
        this.pathManager = pathManager;

        return this;
    }

    public SignaturePathManager getPathManager() {
        return pathManager;
    }

    public boolean isAutoAttachEventListenerEnabled() {
        return autoAttachEventListener;
    }

    public SignatureDialogBuilder setAutoAttachEventListenerEnabled(boolean autoAttachEventListener) {
        this.autoAttachEventListener = autoAttachEventListener;

        return this;
    }

    public void showDialogFragment(
            FragmentManager fragmentManager,
            SignatureEventListener eventListener) {

        showDialogFragment(fragmentManager, SignatureDialogFragment.DEFAULT_DIALOG_TAG, eventListener);
    }

    public void showDialogFragment(FragmentManager fragmentManager) {
        showDialogFragment(fragmentManager, SignatureDialogFragment.DEFAULT_DIALOG_TAG);
    }

    /**
     * Show the dialog.
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *                        or {@link android.app.Fragment}.
     * @author Yordan P. Dieguez
     */
    public void showDialogFragment(
            FragmentManager fragmentManager,
            String tag) {

        showDialogFragment(fragmentManager, tag, null);
    }

    public void showDialogFragment(
            FragmentManager fragmentManager,
            String tag,
            SignatureEventListener eventListener) {

        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY__SIGNATURE_REQUEST, request);
        arguments.putParcelable(KEY__SIGNATURE_RENDERER, renderer);
        arguments.putParcelable(KEY__SIGNATURE_PATH_MANAGER, pathManager);
        arguments.putBoolean(KEY__AUTO_ATTACH_EVENT_LISTENER, autoAttachEventListener);

        SignatureDialogFragment fragment = new SignatureDialogFragment();
        fragment.setArguments(arguments);
        fragment.setSignatureEventListener(eventListener);
        fragment.show(fragmentManager, tag);
    }

    public AlertDialog showStatelessAlertDialog(Activity activity, final SignatureEventListener eventListener) {
        final View view = activity.getLayoutInflater()
                .inflate(R.layout.sig__default_dialog, null, false);

        final SignatureInputView inputView = (SignatureInputView) view.findViewById(R.id.sig__input_view);
        inputView.setSignatureRequest(request);

        if (renderer != null) {
            inputView.setSignatureRenderer(renderer);
        }

        if (pathManager != null) {
            inputView.setPathManager(pathManager);
        }

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.sig__default_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.sig__default_dialog_action_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try{
                            if(!inputView.isSignatureInputAvailable())
                                throw new NoSignatureException("No signature found");

                            SignatureResponse saved = inputView.saveSignature();

                            eventListener.onSignatureEntered(saved);
                        }
                        catch (Exception e){
                            e.printStackTrace();

                            eventListener.onSignatureInputError(e);
                        }
                    }
                })
                .setNegativeButton(R.string.sig__default_dialog_action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eventListener.onSignatureInputCanceled();
                    }
                })
                .show();

        view.findViewById(R.id.sig__action_undo)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        inputView.undoLastSignaturePath();
                    }
                });

        return dialog;
    }
}