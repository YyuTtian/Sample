package com.qgl;

import com.google.gson.Gson;

import java.io.Closeable;
import java.io.Flushable;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class OkFind {

    private static final List<String> realCallList = new ArrayList<>();
    private static final List<String> requestList = new ArrayList<>();
    private static final List<String> requestBodyList = new ArrayList<>();
    private static final List<String> headerList = new ArrayList<>();

    private static final List<String> bufferList = new ArrayList<>();
    private static final List<String> bufferSourceList = new ArrayList<>();
    private static final List<String> bufferSinkList = new ArrayList<>();


    private static final List<String> responseList = new ArrayList<>();
    private static final List<String> responseBodyList = new ArrayList<>();


    /**
     * classList.add(Class.forName("okhttp3.internal.connection.RealCall"));
     * classList.add(Class.forName("okhttp3.Request"));
     * classList.add(Class.forName("okhttp3.RequestBody"));
     * classList.add(Class.forName("okhttp3.Headers"));
     * <p>
     * classList.add(Class.forName("okio.Buffer"));
     * classList.add(Class.forName("okio.BufferedSource"));
     * classList.add(Class.forName("okio.BufferedSink"));
     * <p>
     * classList.add(Class.forName("okhttp3.Response"));
     * classList.add(Class.forName("okhttp3.ResponseBody"));
     */

    public static void find(List<Class<?>> clazzList) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    findImpl(clazzList);
                } catch (Throwable e) {

                }
            }
        }).start();
    }

    public static class Info {
        public List<String> realCall;

        public List<String> request;
        public List<String> requestBody;
        public List<String> headers;

        public List<String> buffer;
        public List<String> bufferSource;
        public List<String> bufferSink;

        public List<String> response;
        public List<String> responseBody;
    }

    private static void findImpl(List<Class<?>> clazzList) throws Throwable {

        realCallList.clear();
        requestList.clear();
        requestBodyList.clear();
        headerList.clear();

        bufferList.clear();
        bufferSourceList.clear();
        bufferSinkList.clear();

        responseList.clear();
        responseBodyList.clear();

        for (Class<?> clazz : clazzList) {
            try {
                findRealCall(clazz);
                findRequest(clazz);
                findRequestBody(clazz);
                findHeader(clazz);

                findBuffer(clazz);
                findBufferedSource(clazz);
                findBufferedSink(clazz);

                findResponse(clazz);
                findResponseBody(clazz);
            } catch (Throwable throwable) {
                System.out.println("find fail " + throwable.getMessage());
                throwable.printStackTrace();
            }
        }

        List<String> filterRequestList = new ArrayList<>();
        List<String> filterResponseList = new ArrayList<>();

        for (String realCall : realCallList) {
            Class<?> realCallClazz = Class.forName(realCall);

            // method=getOriginalRequest return=okhttp3.Request  isFinal=true isPublic=true paramsCount=0
            // method=getResponseWithInterceptorChain$okhttp return=okhttp3.Response isFinal=true isPublic=true paramsCount=0
            Method[] realCallDeclaredMethods = realCallClazz.getDeclaredMethods();
            for (Method realCallDeclaredMethod : realCallDeclaredMethods) {
                if (realCallDeclaredMethod.getParameterCount() == 0) {
                    String typeName = realCallDeclaredMethod.getReturnType().getTypeName();

                    if (requestList.contains(typeName)) {
                        filterRequestList.add(typeName);
                    }

                    if (responseList.contains(typeName)) {
                        filterResponseList.add(typeName);
                    }
                }
            }
        }

        List<String> filterHeaderList = new ArrayList<>();
        List<String> filterRequestBodyList = new ArrayList<>();
        for (String request : filterRequestList) {
            Class<?> requestClazz = Class.forName(request);

            Method[] declaredMethods = requestClazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                String typeName = declaredMethod.getReturnType().getTypeName();

                if (headerList.contains(typeName)) {
                    filterHeaderList.add(typeName);
                }

                if (requestBodyList.contains(typeName)) {
                    filterRequestBodyList.add(typeName);
                }
            }
        }

        List<String> filterResponseBodyList = new ArrayList<>();
        for (String response : filterResponseList) {
            Class<?> responseClazz = Class.forName(response);

            Method[] declaredMethods = responseClazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                String typeName = declaredMethod.getReturnType().getTypeName();

                if (responseBodyList.contains(typeName)) {
                    filterResponseBodyList.add(typeName);
                }
            }
        }

        Info info = new Info();
        info.realCall = realCallList;
        info.request = filterRequestList;
        info.requestBody = filterRequestBodyList;
        info.headers = filterHeaderList;
        info.buffer = bufferList;
        info.bufferSource = bufferSourceList;
        info.bufferSink = bufferSinkList;
        info.response = filterResponseList;
        info.responseBody = filterResponseBodyList;

        String json = new Gson().toJson(info);
        System.out.println(json);
    }

    private static void findRealCall(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaces = clazz.getInterfaces();

        if (Modifier.isFinal(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaces.length == 1) {

            Field[] declaredFields = clazz.getDeclaredFields();
            boolean haveAtomicBoolean = false;
            for (Field declaredField : declaredFields) {
                // field=executed type=java.util.concurrent.atomic.AtomicBoolean
                if (AtomicBoolean.class.isAssignableFrom(declaredField.getType())) {
                    haveAtomicBoolean = true;
                }
            }

            if (haveAtomicBoolean) {
                realCallList.add(clazz.getTypeName());
            }
        }
    }

    private static void findRequest(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isFinal(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaceList.length == 0) {

            boolean have1 = false;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                // method=newBuilder return=okhttp3.Request$Builder
                if (declaredMethod.getReturnType().getTypeName().contains("$")) {
                    have1 = true;
                }
            }
            if (have1) {
                requestList.add(clazz.getTypeName());
            }
        }
    }

    private static void findResponse(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isFinal(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaceList.length == 1
                && Closeable.class.isAssignableFrom(clazz)) {

            boolean fieldTypeIsSelf = false;
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.getType().equals(clazz)) {
                    fieldTypeIsSelf = true;
                }
            }
            if (fieldTypeIsSelf) {
                responseList.add(clazz.getTypeName());
            }
        }
    }

    private static void findBuffer(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isFinal(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaceList.length == 4
                && Cloneable.class.isAssignableFrom(clazz)
                && ByteChannel.class.isAssignableFrom(clazz)
        ) {
            bufferList.add(clazz.getTypeName());
        }
    }

    private static void findRequestBody(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isAbstract(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaceList.length == 0) {

            // create 方法的返回值还是okhttp3.RequestBody
            int createMethodCount = 0;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getReturnType().equals(clazz)) {
                    createMethodCount++;
                }
            }
            if (createMethodCount > 3) {
                requestBodyList.add(clazz.getTypeName());
            }
        }
    }

    private static void findHeader(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isFinal(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaceList.length == 2
                && Iterable.class.isAssignableFrom(clazz)
        ) {

            boolean haveMethod1 = false;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                // method=toMultimap return=java.util.Map
                if (Map.class.isAssignableFrom(declaredMethod.getReturnType()) && declaredMethod.getParameterCount() == 0) {
                    haveMethod1 = true;
                }
            }

            if (haveMethod1) {
                headerList.add(clazz.getTypeName());
            }
        }

    }

    private static void findResponseBody(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isAbstract(modifiers)
                && Modifier.isPublic(modifiers)
                && superClassName.equals("java.lang.Object")
                && interfaceList.length == 1
                && Closeable.class.isAssignableFrom(clazz)) {

            // create 方法的返回值还是okhttp3.ResponseBody
            // method=byteStream return=java.io.InputStream
            int returnTypeIsSelfCount = 0;
            boolean haveByteStreamMethod = false;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {

                if (declaredMethod.getReturnType().equals(clazz)) {
                    returnTypeIsSelfCount++;
                }

                if (InputStream.class.isAssignableFrom(declaredMethod.getReturnType())) {
                    haveByteStreamMethod = true;
                }
            }

            if (returnTypeIsSelfCount > 3 && haveByteStreamMethod) {
                responseBodyList.add(clazz.getTypeName());
            }
        }
    }

    private static void findBufferedSource(Class<?> clazz) {
        ClassInfoUtil.print(clazz);

        int modifiers = clazz.getModifiers();
        String superClassName = getSuperclassName(clazz);
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isAbstract(modifiers)
                && Modifier.isInterface(modifiers)
                && Modifier.isPublic(modifiers)
                && interfaceList.length == 2
                && ReadableByteChannel.class.isAssignableFrom(clazz)
        ) {
            // method=peek return=okio.BufferedSource 方法的返回值类型是自己
            boolean haveReturnSelf = false;
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getReturnType().equals(clazz)) {
                    haveReturnSelf = true;
                }
            }

            if (haveReturnSelf) {
                bufferSourceList.add(clazz.getTypeName());
            }
        }
    }

    private static void findBufferedSink(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        Class<?>[] interfaceList = clazz.getInterfaces();

        if (Modifier.isAbstract(modifiers)
                && Modifier.isInterface(modifiers)
                && Modifier.isPublic(modifiers)
                && interfaceList.length == 2
                && WritableByteChannel.class.isAssignableFrom(clazz)
                && Closeable.class.isAssignableFrom(clazz)
                && Flushable.class.isAssignableFrom(clazz)
        ) {
            bufferSinkList.add(clazz.getTypeName());
        }
    }

    private static String getSuperclassName(Class<?> clazz) {
        String superClassName = "";
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            superClassName = superClass.getTypeName();
        }

        return superClassName;
    }
}
