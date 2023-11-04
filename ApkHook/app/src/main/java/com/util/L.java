package com.util;

import android.util.Log;

import com.hook.MainHook;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class L {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public synchronized static void i(String log) {
        try {
            String date = format.format(new Date(System.currentTimeMillis()));
            File file = new File(MainHook.context.getExternalCacheDir(), "log.txt");
            String txt = "\n\n\n" + date + "\n" + log;
            FileUtils.write(file, txt, true);
        } catch (IOException e) {
            Log.i("L.i", "log fail " + e.getMessage());
        }
    }
}
