package com.hook_okhttp;

import static com.hook_okhttp.OkCompat.Cls_okio_Buffer;
import static com.hook_okhttp.OkCompat.M_buffer_copyTo;
import static com.hook_okhttp.OkCompat.M_buffer_exhausted;
import static com.hook_okhttp.OkCompat.M_buffer_readString;
import static com.hook_okhttp.OkCompat.M_buffer_readUtf8CodePoint;
import static com.hook_okhttp.OkCompat.M_buffer_size;
import static com.hook_okhttp.OkCompat.M_reqbody_writeTo;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

import android.util.Log;

import com.util.L;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class LogFormat {

    private static final String TAG = "LogFormat";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String br = "\n";

    private static String logStr = "";

    public static void log(Object request, Object response, ClassLoader classLoader, String content) {
        try {
            logStr = "";
            Object method = getObjectField(request, "method");
            Object url = getObjectField(request, "url");
            Object requestHeaders = getObjectField(request, "headers");
            Object requestBody = getObjectField(request, "body");

            Map<String, List<String>> requestHeadData = (Map<String, List<String>>) (callMethod(requestHeaders, "toMultimap"));

            logStr += "请求 " + method + " " + url + br;
            for (Map.Entry<String, List<String>> stringListEntry : requestHeadData.entrySet()) {
                String headKey = stringListEntry.getKey();
                StringBuilder headValue = new StringBuilder();
                for (String s : stringListEntry.getValue()) {
                    headValue.append(s).append(" ");
                }
                logStr += headKey + ":" + headValue + br;
            }

            if (requestBody != null) {
                String contentType = callMethod(requestBody, "contentType").toString();
                logStr += "Content-Type:" + contentType + br;

                long contentLength = (long) callMethod(requestBody, "contentLength");
                if (contentLength > 0) {
                    logStr += "Content-Length:" + contentLength + br;
                }


                Class<?> BufferClass = findClass(Cls_okio_Buffer, classLoader);
                Object buffer = newInstance(BufferClass);
                callMethod(requestBody, M_reqbody_writeTo, buffer);

                if (isPlaintext(buffer, classLoader)) {
                    String str = (String) callMethod(buffer, M_buffer_readString, UTF8);
                    str = HandleBodyString.request(str);
                    logStr += br + str + br;
                }
            }

            logStr += br + "-------------------------------------分割线-------------------------------------" + br;
            String code = getObjectField(response, "code").toString();
            String message = getObjectField(response, "message").toString();
            logStr += br + "响应 " + code + " " + message + br;
            Object responseHeaders = getObjectField(response, "headers");
            Map<String, List<String>> responseHeadData = (Map<String, List<String>>) (callMethod(responseHeaders, "toMultimap"));
            for (Map.Entry<String, List<String>> stringListEntry : responseHeadData.entrySet()) {
                String headKey = stringListEntry.getKey();
                StringBuilder headValue = new StringBuilder();
                for (String s : stringListEntry.getValue()) {
                    headValue.append(s).append(" ");
                }
                logStr += headKey + ":" + headValue + br;
            }

            Object responseBody = getObjectField(response, "body");
            String contentType = callMethod(responseBody, "contentType").toString();
            logStr += "Content-Type:" + contentType + br;

            long contentLength = (long) callMethod(responseBody, "contentLength");
            if (contentLength > 0) {
                logStr += "Content-Length:" + contentLength + br;
            }

            content = HandleBodyString.response(content);
            logStr += br + content + br;

            Log.i(TAG, logStr);

            L.i(logStr);
        } catch (Throwable throwable) {
            Log.i(TAG, "log fail " + throwable.getMessage() + " " + throwable);
        }
    }


    private static boolean isPlaintext(Object buffer, ClassLoader classLoader) {
        try {
            Class<?> BufferClass = findClass(Cls_okio_Buffer, classLoader);
            Object prefix = newInstance(BufferClass);
            long size = (Long) callMethod(buffer, M_buffer_size);
            long byteCount = size < 64 ? size : 64;
            callMethod(buffer, M_buffer_copyTo, prefix, 0l, byteCount);
            for (int i = 0; i < 16; i++) {
                if ((boolean) callMethod(prefix, M_buffer_exhausted)) {
                    break;
                }
                int codePoint = (int) callMethod(prefix, M_buffer_readUtf8CodePoint);
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
