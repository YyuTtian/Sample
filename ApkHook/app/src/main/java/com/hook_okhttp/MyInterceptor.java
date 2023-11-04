package com.hook_okhttp;


import android.util.Log;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.hook_okhttp.OkCompat.*;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class MyInterceptor {

    private static final String TAG = OkHttpHooker.TAG;

    private final Builder builder;

    private MyInterceptor(Builder builder) {
        this.builder = builder;
    }

    private Object newInterceptor(final ClassLoader classLoader) {
        Class<?> InterceptorClass = findClass(Cls_Interceptor, classLoader);
        Object myInterceptor = Proxy.newProxyInstance(classLoader, new Class[]{InterceptorClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (M_Interceptor_intercept.equals(method.getName())) {
                    synchronized (MyInterceptor.class) {
                        try {
                            Object chain = objects[0];
                            Object request = addQueryAndHeaders(callMethod(chain, M_chain_request));

                            Object response = null;
                            try {
//                                chain.getClass().getMethod(M_chain_proceed);
//                                Method chain_proceed = findMethodExact(chain.getClass(), M_chain_proceed, request);
//                                response = chain_proceed.invoke(chain, request);
//                                Log.d("sdfafs", "declaredMethod开始");
//                                for (Method declaredMethod : chain.getClass().getDeclaredMethods()) {
//                                    Log.d("sdfafs", "declaredMethod=" + declaredMethod.getName());
//                                }
//                                Log.d("sdfafs", "declaredMethod结束");

                                response = callMethod(chain, M_chain_proceed, request);
                                Object responseBody = callMethod(response, M_rsp_body);
                                String content = (String) callMethod(responseBody, M_rspBody_string);

                                LogFormat.log(request, response, classLoader, content);

                                Object mediaType = callMethod(responseBody, M_rspBody_contentType);
                                Class<?> ok3ResponseBodyClass = findClass(Cls_ResponseBody, classLoader);
                                Object newBody = callStaticMethod(ok3ResponseBodyClass, M_rspBody_create, mediaType, content);
                                Object newBuilder = callMethod(response, M_rsp_newBuilder);
                                return callMethod(callMethod(newBuilder, M_rsp$builder_body, newBody), M_rsp$builder_build);

                            } catch (Throwable e) {
                                Log.i("sdfafs", "fail 1 " + e.getMessage() + " " + e);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, Log.getStackTraceString(e));
                        }
                    }
                }
                return method.invoke(o, objects);
            }
        });
        return myInterceptor;
    }

    private Object addQueryAndHeaders(Object request) {
        Object requestBuilder = callMethod(request, M_req_newBuilder);
        Set<String> header_keySet = builder.headers.keySet();
        for (String key : header_keySet) {
            callMethod(requestBuilder, M_Request$Builder_addHeader, key, builder.headers.get(key));//b
        }

        Object httpUrl = callMethod(request, M_req_url);

        String url = (String) callMethod(httpUrl, M_httpUrl_toString);

        Object httpUrlBuilder = callMethod(httpUrl, M_httpUrl_newBuilder, url);

        Set<String> query_keySet = builder.queryMap.keySet();
        for (String key : query_keySet) {
            callMethod(httpUrlBuilder, M_httpUrl_addQueryParameter, key, builder.queryMap.get(key));
        }
        return callMethod(callMethod(requestBuilder, M_Request$Builder_url, callMethod(httpUrlBuilder, M_httpUrl_build)), M_Request$Builder_build);
    }


    static class Builder {
        private final Map<String, String> headers;
        private final Map<String, String> queryMap;

        public Builder() {
            this.headers = new HashMap<>();
            this.queryMap = new HashMap<>();
        }

        public Builder addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder addQueryParam(String key, String value) {
            queryMap.put(key, value);
            return this;
        }

        public Object build(ClassLoader classLoader) {
            return new MyInterceptor(this).newInterceptor(classLoader);
        }
    }


}
