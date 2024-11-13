package com.qgl;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OkParse {

    /**
     * private fun parseResponse(response: Response) {
     * <p>
     * val method = response.request.method
     * val url = response.request.url
     * <p>
     * response.request.headers.toMultimap().entries.forEach {
     * <p>
     * }
     * <p>
     * val buffer = okio.Buffer()
     * response.request.body?.writeTo(buffer)
     * val requestBodyStr = buffer.readUtf8()
     * <p>
     * <p>
     * <p>
     * <p>
     * response.headers.toMultimap().entries.forEach {
     * <p>
     * }
     * val source = response.body?.source()
     * source?.request(Long.MAX_VALUE)
     * val responseStr = source?.buffer?.clone()?.readUtf8()
     * <p>
     * }
     *
     * @param response
     */
    private static final Gson gson = new Gson();

    public static void parse(Object response, String classJson, boolean isDebug) {
        StringBuilder sb = new StringBuilder();
        OkFind.Info info = gson.fromJson(classJson, OkFind.Info.class);
        Field[] fields = response.getClass().getFields();

        for (Field field : fields) {
            String typeName = field.getType().getTypeName();
            if (info.request.contains(typeName)) {
                field.setAccessible(true);
                try {
                    Object request = field.get(response);
                    parseRequest(request, info, sb, isDebug);
                } catch (Throwable throwable) {
                }
            }
        }

        parseResponse(response, info, sb, isDebug);
    }

    private static void parseRequest(Object request, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        try {
            // 解析请求url
            parseUrl(request, sb, isDebug);

            Field[] declaredFields = request.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object fieldObj = declaredField.get(request);

                String typeName = declaredField.getType().getTypeName();

                if (info.headers.contains(typeName)) {
                    // 解析请求头
                    parseHeaders(fieldObj, sb, isDebug);
                } else if (info.requestBody.contains(typeName)) {
                    // 解析请求体
                    parseRequestBody(fieldObj, info, sb, isDebug);
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseRequest fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static void parseUrl(Object request, StringBuilder sb, boolean isDebug) {
        try {
            // response.request.url
            Field[] declaredFields = request.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object fieldObj = declaredField.get(request);
                if (fieldObj.toString().startsWith("http")) {
                    sb.append(fieldObj).append("\n");
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseUrl fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static void parseHeaders(Object headers, StringBuilder sb, boolean isDebug) {
        try {
            // response.request.headers.toMultimap().entries.forEach
            Method[] declaredMethods = headers.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                if (Map.class.isAssignableFrom(declaredMethod.getReturnType())) {
                    Map<String, List<String>> headMap = (Map<String, List<String>>) declaredMethod.invoke(headers);

                    for (Map.Entry<String, List<String>> entry : headMap.entrySet()) {
                        StringBuilder value = new StringBuilder();

                        for (String s : entry.getValue()) {
                            value.append(s).append(" ");
                        }

                        sb.append(entry.getKey()).append(":").append(value);
                    }
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseHeaders fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static void parseRequestBody(Object requestBody, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        // val buffer = okio.Buffer()
        // response.request.body?.writeTo(buffer)
        // val requestBodyStr = buffer.readUtf8()


//        @Throws(IOException::class)
//        abstract fun writeTo(sink: BufferedSink)
        List<Method> writeToMethodList = getWriteToMethod(requestBody, info, sb, isDebug);
        List<Object> bufferObjList = wieteToBuffGetBufferObj(requestBody, writeToMethodList, info, sb, isDebug);
        for (Object bufferObj : bufferObjList) {
            callBufferReadUtf8Method(bufferObj, sb, isDebug);
        }
    }

    private static List<Method> getWriteToMethod(Object requestBody, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        List<Method> resultList = new ArrayList<>();
        try {
            Method[] declaredMethods = requestBody.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);

                for (String bufferSink : info.bufferSink) {
                    Class<?> bufferSinkClass = Class.forName(bufferSink);
                    if (declaredMethod.getParameterCount() == 1 &&
                            bufferSinkClass.isAssignableFrom(declaredMethod.getParameters()[0].getType())) {
                        resultList.add(declaredMethod);
                    }
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("getWriteToMethod fail ").append(throwable.getMessage()).append("\n");
        }
        return resultList;
    }

    private static List<Object> wieteToBuffGetBufferObj(Object requestBody, List<Method> methodList, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        List<Object> resultList = new ArrayList<>();
        try {
            for (Method method : methodList) {
                for (String buffer : info.buffer) {
                    Class<?> bufferClass = Class.forName(buffer);
                    Object buffObj = bufferClass.newInstance();
                    method.invoke(requestBody, buffObj);
                    resultList.add(buffObj);
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("getWriteToMethod fail ").append(throwable.getMessage()).append("\n");
        }

        return resultList;
    }

    private static void parseResponse(Object response, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        try {
            parseResponseHead(response, info, sb, isDebug);
            parseResponseBody(response, info, sb, isDebug);
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseResponse fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static void parseResponseHead(Object response, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        try {
            Field[] declaredFields = response.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object fieldObj = declaredField.get(response);
                String typeName = declaredField.getType().getTypeName();

                if (info.headers.contains(typeName)) {
                    parseHeaders(fieldObj, sb, isDebug);
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseResponseHead fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static void parseResponseBody(Object response, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        try {
            Field[] declaredFields = response.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Object fieldObj = declaredField.get(response);

                String typeName = declaredField.getType().getTypeName();

                if (info.responseBody.contains(typeName)) {

                    // fieldObj是responseBody
                    // val source = response.body?.source()
                    parseResponseBodySource(fieldObj, info, sb, isDebug);
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseResponseBody fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static void parseResponseBodySource(Object responseBody, OkFind.Info info, StringBuilder sb, boolean isDebug) {
        try {
            /**
             *       val source = response.body?.source()
             *      * source?.request(Long.MAX_VALUE)
             *      * val responseStr = source?.buffer?.clone()?.readUtf8()
             */
            Method[] responseBodyMethods = responseBody.getClass().getDeclaredMethods();
            for (Method responseBodyMethod : responseBodyMethods) {
                responseBodyMethod.setAccessible(true);
                if (responseBodyMethod.getParameterCount() == 0) {
                    //   abstract fun source(): BufferedSource
                    for (String bufferSourceClassName : info.bufferSource) {
                        if (Class.forName(bufferSourceClassName).isAssignableFrom(responseBodyMethod.getReturnType())) {
                            // 调用source方法得到BufferedSource对象
                            Object sourceObj = responseBodyMethod.invoke(responseBody);
                            // source.request(Long.MAX_VALUE)
                            setSourceRequestMethod(sourceObj, sb, isDebug);
                            // source.buffer
                            List<Object> bufferList = getSourceBufferField(sourceObj, info, sb, isDebug);

                            for (Object bufferObj : bufferList) {
                                // source.buffer.clone()
                                List<Object> cloneBufferList = callCloneMethod(bufferObj, sb, isDebug);
                                for (Object cloneBuffer : cloneBufferList) {
                                    // source.buffer.clone().readUtf8()
                                    callBufferReadUtf8Method(cloneBuffer, sb, isDebug);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("parseResponseBodySource fail ").append(throwable.getMessage()).append("\n");
        }
    }


    private static void setSourceRequestMethod(Object sourceObj, StringBuilder sb, boolean isDebug) {
        try {
            // source.request(Long.MAX_VALUE)
            Method[] sourceMethods = sourceObj.getClass().getDeclaredMethods();
            for (Method sourceMethod : sourceMethods) {
                sourceMethod.setAccessible(true);
                //  actual fun request(byteCount: Long): Boolean
                if (sourceMethod.getParameterCount() == 1 &&
                        sourceMethod.getParameters()[0].getType().getTypeName().equals("long") &&
                        sourceMethod.getReturnType().getTypeName().equals("boolean")) {
                    // source的request方法
                    sourceMethod.invoke(sourceObj, Long.MAX_VALUE);
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("setSourceRequestMethod fail ").append(throwable.getMessage()).append("\n");
        }
    }

    private static List<Object> getSourceBufferField(Object sourceObj, OkFind.Info info, StringBuilder sb, boolean isDebug) {

        List<Object> resultList = new ArrayList<>();

        try {
            // source.buffer
            Field[] sourceFields = sourceObj.getClass().getDeclaredFields();
            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                String sourceFieldTypeName = sourceField.getType().getTypeName();
                if (info.buffer.contains(sourceFieldTypeName)) {
                    resultList.add(sourceField.get(sourceObj));
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("getSourceBufferField fail ").append(throwable.getMessage()).append("\n");
        }

        return resultList;
    }

    private static List<Object> callCloneMethod(Object bufferObj, StringBuilder sb, boolean isDebug) {
        // source.buffer.clone()
        List<Object> resultList = new ArrayList<>();

        try {
            Method[] declaredMethods = bufferObj.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                // public override fun clone(): Buffer = copy()
                if (declaredMethod.getParameterCount() == 0 && declaredMethod.getReturnType().equals(bufferObj.getClass())) {
                    resultList.add(declaredMethod.invoke(bufferObj));
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("callCloneMethod fail ").append(throwable.getMessage()).append("\n");
        }

        return resultList;
    }

    private static void callBufferReadUtf8Method(Object bufferObj, StringBuilder sb, boolean isDebug) {
        try {
            //   override fun readUtf8() = readString(size, Charsets.UTF_8)
            Method[] declaredMethods = bufferObj.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                declaredMethod.setAccessible(true);
                if (declaredMethod.getParameterCount() == 0 && String.class.isAssignableFrom(declaredMethod.getReturnType())) {
                    sb.append(declaredMethod.invoke(bufferObj)).append("\n\n\n\n");
                }
            }
        } catch (Throwable throwable) {
            if (isDebug) sb.append("callReadUtf8Method fail ").append(throwable.getMessage()).append("\n");
        }
    }
}
