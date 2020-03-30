package com.guardanis.sigcap;

import androidx.fragment.app.FragmentManager;

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


    /**
     * Show the dialog.
     *
     * @param fragmentManager The {@link FragmentManager} provided by {@link android.app.Activity}
     *                        or {@link androidx.fragment.app.Fragment}.
     * @author Yordan P. Dieguez
     */
    public void show(FragmentManager fragmentManager, String tag) {
        new SignatureDialogFragment()
                .setRequest(request)
                .setSignatureRenderer(renderer)
                .show(fragmentManager, tag);
    }
}
