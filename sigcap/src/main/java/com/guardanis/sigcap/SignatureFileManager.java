package com.guardanis.sigcap;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SignatureFileManager {

    /**
     * @return a {@link File} pointing to a writable subdirectory
     * of {@link Context#getFilesDir()}, or <code>null</code> if the subdirectory
     * does not exist and cannot be created.
     */
    public static File getWritableCacheDirectory(Context context) {
        File cacheDirectory = new File(context.getFilesDir(), "sigcap");

        if (!(cacheDirectory.exists() || cacheDirectory.mkdirs())) {
            return null;
        }

        return cacheDirectory;
    }

    /**
     * @return a new temp {@link File} in the app-private cache subdirectory,
     * or <code>null</code> if the cache subdirectory does not exist and cannot be created.
     */
    public static File createTempFile(Context context) {
        File cacheDirectory = getWritableCacheDirectory(context);

        if (cacheDirectory == null)
            return null;

        try {
            return File.createTempFile("sig", ".png", cacheDirectory);
        }
        catch (IOException e) {
            Log.e("SignatureFileManager", "Could not create temp File", e);

            return null;
        }
    }

    /**
     * Asynchronously delete all files in the app-private cache subdirectory.
     *
     * @return a {@link Future<Boolean>} which will indicate if the task
     * was able to delete all files in the cache subdirectory.
     */
    public static Future<Boolean> deleteAll(Context context) {
        final WeakReference<Context> contextWeak = new WeakReference<Context>(context);

        return Executors.newSingleThreadExecutor()
                .submit(new Callable<Boolean>() {

                    @Override
                    public Boolean call() {
                        final Context context = contextWeak.get();

                        if (context == null)
                            return false;

                        File cacheDirectory = getWritableCacheDirectory(context);

                        if (cacheDirectory == null)
                            return false;

                        File[] files;

                        try {
                            files = cacheDirectory.listFiles();
                        }
                        catch (SecurityException e) {
                            Log.e("SignatureFileManager", "Could not collect cached File(s)", e);

                            return false;
                        }

                        return deleteAll(files);
                    }
                });
    }

    /**
     * Delete every given {@link File} from local storage.
     *
     * @return <code>false</code> if supplied <code>files</code> array is <code>null</code>
     * or any {@link File} is not successfully deleted; <code>true</code> if <code>files</code>
     * array is empty or all <code>files</code> are successfully deleted.
     */
    public static boolean deleteAll(File[] files) {
        if (files == null)
            return false;

        boolean allDeletionsSucceeded = true;

        for (File file : files) {
            try {
                allDeletionsSucceeded &= file.delete();
            }
            catch (SecurityException e) {
                Log.e("SignatureFileManager", "Could not delete file: " + file.getAbsolutePath(), e);

                allDeletionsSucceeded = false;
            }
        }

        return allDeletionsSucceeded;
    }
}