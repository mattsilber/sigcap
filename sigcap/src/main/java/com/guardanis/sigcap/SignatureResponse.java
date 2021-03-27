package com.guardanis.sigcap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SignatureResponse {

    private final Bitmap result;

    public SignatureResponse(Bitmap result) {
        this.result = result;
    }

    /**
     * @return the {@link Bitmap} result
     */
    public Bitmap getResult() {
        return result;
    }

    /**
     * Asynchronously save the generated {@link Bitmap} result
     * to a new {@link File} created from the {@link SignatureFileManager} and
     * return a {@link Future} to it. If the cache subdirectory does not exist
     * and cannot be created, the result of {@link Future<File>#get()} will
     * be <code>null</code>.
     */
    public Future<File> saveToFileCache(Context context) {
        final WeakReference<Context> contextWeak = new WeakReference<Context>(context);

        return Executors.newSingleThreadExecutor()
                .submit(new Callable<File>() {

                    @Override
                    public File call() {
                        final Context context = contextWeak.get();

                        if (context == null)
                            return null;

                        File file = SignatureFileManager.createTempFile(context);

                        if (file == null) {
                            Log.e("SignatureResponse", "Temp File is null; cannot write Bitmap to local storage");

                            return null;
                        }

                        try {
                            result.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                        }
                        catch (SecurityException e) {
                            Log.e("SignatureResponse", "Cannot write to local storage", e);

                            return null;
                        }
                        catch (IOException e) {
                            Log.e("SignatureResponse", "Error writing Bitmap to local storage", e);

                            return null;
                        }

                        return file;
                    }
                });
    }
}
