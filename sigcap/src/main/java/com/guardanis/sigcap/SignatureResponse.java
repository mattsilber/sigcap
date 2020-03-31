package com.guardanis.sigcap;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SignatureResponse {

    private final Bitmap result;

    SignatureResponse(Bitmap result) {
        this.result = result;
    }

    public Bitmap getResult() {
        return result;
    }

    public Future<File> saveToFileCache(final Context context) {
        return Executors.newSingleThreadExecutor()
                .submit(() -> {
                    File file = new FileCache(context)
                            .getFile(String.valueOf(System.currentTimeMillis()));

                    result.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));

                    return file;
                });
    }
}
