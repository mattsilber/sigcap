package com.guardanis.sigcap.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.guardanis.sigcap.R;
import com.guardanis.sigcap.SignatureEventListener;
import com.guardanis.sigcap.SignatureInputView;
import com.guardanis.sigcap.SignatureRenderer;
import com.guardanis.sigcap.SignatureRequest;
import com.guardanis.sigcap.SignatureResponse;
import com.guardanis.sigcap.dialog.events.UndoLastSignatureClickListener;
import com.guardanis.sigcap.exceptions.CanceledSignatureInputException;
import com.guardanis.sigcap.exceptions.NoSignatureException;
import com.guardanis.sigcap.paths.SignaturePathManager;

import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_PATH_MANAGER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_RENDERER;
import static com.guardanis.sigcap.SignatureInputView.KEY__SIGNATURE_REQUEST;
import static com.guardanis.sigcap.dialog.SignatureDialogFragment.KEY__AUTO_ATTACH_EVENT_LISTENER;

/**
 * Helper class to configure and show the {@link SignatureInputView}
 * inside of a {@link android.app.DialogFragment}.
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
     * Pass a {@link SignatureRequest} instance before showing the dialog.
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
     * Pass a {@link SignatureRenderer} instance before showing the dialog.
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

    /**
     * Pass a {@link SignaturePathManager} instance before showing the dialog.
     *
     * @param pathManager {@link SignatureRenderer} object
     * @return {@link SignatureDialogBuilder}
     */
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

    /**
     * Set whether or not the {@link SignatureDialogFragment} should attempt to
     * auto-set the {@link SignatureEventListener} to the {@link Activity}/{@link Fragment}'s parent
     * when attached to an Activity.
     *
     * The default is set to true, and will only log a warning if unset.
     *
     * @param autoAttachEventListener enable auto-attach or disable
     * @return {@link SignatureDialogBuilder}
     */
    public SignatureDialogBuilder setAutoAttachEventListenerEnabled(boolean autoAttachEventListener) {
        this.autoAttachEventListener = autoAttachEventListener;

        return this;
    }

    /**
     * Create a {@link SignatureDialogFragment} and show it using the
     * default tag of SignatureDialogFragment.DEFAULT_DIALOG_TAG
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *      or {@link android.app.Fragment}.
     * @param eventListener The {@link SignatureEventListener} instance
     */
    public void showDialogFragment(
            FragmentManager fragmentManager,
            SignatureEventListener eventListener) {

        showDialogFragment(fragmentManager, SignatureDialogFragment.DEFAULT_DIALOG_TAG, eventListener);
    }

    /**
     * Create a {@link SignatureDialogFragment} and show it using the
     * default tag of SignatureDialogFragment.DEFAULT_DIALOG_TAG
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *      or {@link android.app.Fragment}.
     */
    public void showDialogFragment(FragmentManager fragmentManager) {
        showDialogFragment(fragmentManager, SignatureDialogFragment.DEFAULT_DIALOG_TAG);
    }

    /**
     * Create a {@link SignatureDialogFragment} and show it.
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *      or {@link android.app.Fragment}.
     * @param tag A string to find the created {@link SignatureDialogFragment}
     * @author Yordan P. Dieguez
     */
    public void showDialogFragment(
            FragmentManager fragmentManager,
            String tag) {

        showDialogFragment(fragmentManager, tag, null);
    }

    /**
     * Create a {@link SignatureDialogFragment} and show it.
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *      or {@link android.app.Fragment}.
     * @param tag A string to find the created {@link SignatureDialogFragment}
     * @param eventListener The {@link SignatureEventListener} instance
     */
    public void showDialogFragment(
            FragmentManager fragmentManager,
            String tag,
            SignatureEventListener eventListener) {

        SignatureDialogFragment fragment = new SignatureDialogFragment();
        fragment.setArguments(buildSignatureDialogArguments());
        fragment.setSignatureEventListener(eventListener);
        fragment.show(fragmentManager, tag);
    }

    protected Bundle buildSignatureDialogArguments() {
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY__SIGNATURE_REQUEST, request);
        arguments.putParcelable(KEY__SIGNATURE_RENDERER, renderer);
        arguments.putParcelable(KEY__SIGNATURE_PATH_MANAGER, pathManager);
        arguments.putBoolean(KEY__AUTO_ATTACH_EVENT_LISTENER, autoAttachEventListener);

        return arguments;
    }

    /**
     * Show using a simple AlertDialog.
     */
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
                        try {
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
                        eventListener.onSignatureInputError(
                                new CanceledSignatureInputException());
                    }
                })
                .show();

        view.findViewById(R.id.sig__action_undo)
                .setOnClickListener(
                        new UndoLastSignatureClickListener(inputView));

        return dialog;
    }
}
