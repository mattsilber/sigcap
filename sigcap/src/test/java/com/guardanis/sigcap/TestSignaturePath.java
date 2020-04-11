package com.guardanis.sigcap;

import com.guardanis.sigcap.paths.SignaturePath;

public class TestSignaturePath extends SignaturePath {

    @Override
    public void movePathTo(Float[] coordinates) {
        super.movePathTo(coordinates);
    }

    @Override
    public void addPathLineTo(Float[] coordinates) {
        super.addPathLineTo(coordinates);
    }
}
