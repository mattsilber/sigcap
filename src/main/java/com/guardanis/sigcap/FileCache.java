package com.guardanis.sigcap;

import android.content.Context;

import java.io.File;

public class FileCache {

    private File cacheDir;

    public FileCache(Context context) {
        if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), context.getPackageName());
        else cacheDir = context.getCacheDir();

        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        return new File(cacheDir, String.valueOf(url.hashCode()));
    }

    public void clear() {
        File[] files = cacheDir.listFiles();

        if(files != null)
            for(File file : files)
                file.delete();
    }

    public static void clear(Context context){
        new FileCache(context)
                .clear();
    }
}