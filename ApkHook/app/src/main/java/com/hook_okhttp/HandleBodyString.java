package com.hook_okhttp;

import android.util.Base64;

import com.hook.MainHook;

import org.json.JSONObject;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import kotlin.text.Charsets;

public class HandleBodyString {

    public static String request(String str) {
        return str;
    }

    public static String response(String str) {
        if (MainHook.context.getPackageName().equals("xxx")) {
            try {
                String data = new JSONObject(str).getString("data");
                return decryptData1(data);
            } catch (Throwable ignored) {
            }
        }
        return str;
    }

    private static String decryptData(String str) {
        try {
            String str2 = "9U6XDekzOyJAcEmv";
            String str3 = "AES";

            byte[] bytes = str2.getBytes(Charsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "AES");
            Cipher cipher = Cipher.getInstance(str3);
            cipher.init(2, secretKeySpec);
            byte[] decode = Base64.decode(str, 0);
            byte[] doFinal = cipher.doFinal(decode);

            Charset defaultCharset = Charset.defaultCharset();
            return new String(doFinal, defaultCharset);
        } catch (Throwable throwable) {
        }
        return null;
    }
}
