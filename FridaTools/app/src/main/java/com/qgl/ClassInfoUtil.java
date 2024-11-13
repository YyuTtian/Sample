package com.qgl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class ClassInfoUtil {

    public static void print(Class<?> clazz) {

        if (true) return;

        StringBuilder sb = new StringBuilder();

        sb.append("className=").append(clazz.getTypeName()).append("\n");

        int modifiers = clazz.getModifiers();
        modifierInfo(modifiers, sb);

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            String superClassName = superClass.getTypeName();
            sb.append("superClassName=").append(superClassName).append("\n");
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        sb.append("interface count=").append(interfaces.length).append("\n");
        for (Class<?> anInterface : interfaces) {
            sb.append("interface name=").append(anInterface.getTypeName());
        }

        sb.append("\n\n\n\n");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int fieldModifier = field.getModifiers();
            sb.append("field=").append(field.getName()).append(" type=").append(field.getType().getTypeName()).append("\n");
            modifierInfo(fieldModifier, sb);
            annotationInfo(field.getAnnotations(), sb);
            sb.append("\n\n");
        }

        sb.append("\n\n");
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            int methodModifiers = method.getModifiers();
            sb.append("method=").append(method.getName()).append(" return=").append(method.getReturnType().getTypeName()).append("\n");
            modifierInfo(methodModifiers, sb);
            annotationInfo(method.getAnnotations(), sb);
            paramsInfo(method, sb);
            sb.append("\n\n");
        }

        System.out.println(sb);

    }

    private static void paramsInfo(Method method, StringBuilder sb) {
        Parameter[] parameters = method.getParameters();
        sb.append("paramsCount=").append(parameters.length).append("\n");
        for (int i = 0; i < parameters.length; i++) {
            sb.append("params_").append(i + 1).append(" ").append(parameters[i].getType().getTypeName()).append("\n");
        }
    }

    private static void annotationInfo(Annotation[] annotations, StringBuilder sb) {
        for (Annotation annotation : annotations) {
            sb.append("annotation=").append(annotation.annotationType().getTypeName()).append("\n");
        }
    }

    private static void modifierInfo(int modifiers, StringBuilder sb) {
        boolean isAbstract = Modifier.isAbstract(modifiers);
        boolean isInterface = Modifier.isInterface(modifiers);
        boolean isFinal = Modifier.isFinal(modifiers);
        boolean isNative = Modifier.isNative(modifiers);
        boolean isPublic = Modifier.isPublic(modifiers);
        boolean isPrivate = Modifier.isPrivate(modifiers);
        boolean isProtected = Modifier.isProtected(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        boolean isSynchronized = Modifier.isSynchronized(modifiers);
        boolean isVolatile = Modifier.isVolatile(modifiers);

        if (isAbstract) sb.append("isAbstract=").append(true).append("\n");
        if (isInterface) sb.append("isInterface=").append(true).append("\n");
        if (isFinal) sb.append("isFinal=").append(true).append("\n");
        if (isNative) sb.append("isNative=").append(true).append("\n");
        if (isPublic) sb.append("isPublic=").append(true).append("\n");
        if (isPrivate) sb.append("isPrivate=").append(true).append("\n");
        if (isProtected) sb.append("isProtected=").append(true).append("\n");
        if (isStatic) sb.append("isStatic=").append(true).append("\n");
        if (isSynchronized) sb.append("isSynchronized=").append(true).append("\n");
        if (isVolatile) sb.append("isVolatile=").append(true).append("\n");
    }
}
