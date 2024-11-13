package com.qgl;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

public class JavaUtil {

    private static final Gson gson = new Gson();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String obj2Json(Object obj) {
        try {
            return gson.toJson(obj);
        } catch (Throwable throwable) {
            return "obj2Json fail " + throwable.getMessage();
        }
    }

    public static void openActivity(Context context, String className) {
        try {
            Intent intent = new Intent(context, Class.forName(className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable throwable) {
        }
    }

    public static String str2Base64(String str) {
        return ByteString.of(str.getBytes()).base64();
    }

    public static String base642Str(String base64) {
        return String.valueOf(ByteString.decodeBase64(base64));
    }

    public static String byteArray2String(byte[] bytes) {
        return new String(bytes);
    }

    public static String byteArray2Hex(byte[] bytes) {
        return ByteString.of(bytes).hex();
    }

    public static String byteArray2Base64(byte[] bytes) {
        return ByteString.of(bytes).base64();
    }

    private static File logFile;

    private static Context context;

    public static void initLogFile(Context _context) {
        context = _context;
        logFile = new File(context.getExternalCacheDir(), "frida_log.txt");
    }

    public static void writeLogFile(String str) {
        try {
            String date = dateFormat.format(new Date());
            List<String> lines = new ArrayList<>();
            lines.add(date);
            lines.add(str);
            FileUtils.writeLines(logFile, lines, true);
        } catch (Throwable ignored) {
        }
    }

    public static String showStack() {
        return Log.getStackTraceString(new Throwable());
    }

    public static boolean haveStr(Object obj, String findStr) {
        try {
            return obj.toString().contains(findStr);
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static void findOkHttp(List<Class<?>> clazzList) {
        OkFind.find(clazzList);
    }


    public static void parseOkHttp(Object response, String classJson, boolean isDebug) {
        OkParse.parse(response, classJson, isDebug);
    }
}