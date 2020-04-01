package com.guardanis.sigcap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.view.View;

import com.guardanis.sigcap.paths.SignaturePathManager;

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
    public SignatureDialogBuilder setSignatureRenderer(SignatureRenderer renderer) {
        this.renderer = renderer;

        return this;
    }

    public SignatureRenderer getSignatureRenderer() {
        return renderer;
    }

    public SignatureDialogBuilder setPathManager(SignaturePathManager pathManager) {
        this.pathManager = pathManager;

        return this;
    }

    public SignaturePathManager getPathManager() {
        return pathManager;
    }

    /**
     * Show the dialog.
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *                        or {@link android.app.Fragment}.
     * @author Yordan P. Dieguez
     */
    public void show(FragmentManager fragmentManager, String tag) {
        new SignatureDialogFragment()
                .setRequest(request)
                .setSignatureRenderer(renderer)
                .setPathManager(pathManager)
                .show(fragmentManager, tag);
    }

    public AlertDialog showStateless(Activity activity, final SignatureEventListener eventListener) {
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
