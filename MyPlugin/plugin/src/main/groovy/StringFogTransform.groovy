import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.*

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static org.objectweb.asm.Opcodes.*

class StringFogTransform extends Transform {

    Project project

    StringFogTransform(Project _project) {
        project = _project
    }

    @Override
    String getName() {
        return "StringFog"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws Throwable {
        Config config = project.extensions.findByType(Config.class)
        if (config != null && config.enable) {
            Collection<TransformInput> inputs = transformInvocation.inputs
            TransformOutputProvider outs = transformInvocation.outputProvider

            // 删除之前的输出
            if (outs != null) {
                outs.deleteAll()
            }

            inputs.each { TransformInput input ->
                input.jarInputs.each { JarInput jarInput ->
                    handleJarInputs(jarInput, outs)
                }

                input.directoryInputs.each { DirectoryInput directoryInput ->
                    handleDirectoryInput(directoryInput, outs)
                }
            }

        } else {
            super.transform(transformInvocation);
        }
    }

    private void handleJarInputs(JarInput jarInput, TransformOutputProvider outputProvider) {
        println "_qgllog jarInput=" + jarInput.file.getAbsolutePath()
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            // 重名输出文件,因为可能同名,会覆盖
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())

            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }

            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            // 避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }

            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))

            // 遍历
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)

                // 处理class
                if (entryName.endsWith(".class") &&
                        !entryName.startsWith("R\$") &&
                        !entryName.equals("R.class") &&
                        !entryName.equals("BuildConfig.class")) {

                    jarOutputStream.putNextEntry(zipEntry)

                    // class文件处理
                    println "_qgllog deal with jar class file " + entryName

                    // entryName是class文件的全路径  把/替换成.  然后把后面的.class去掉
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6)


                    if (isCanFog(className)) {
                        jarOutputStream.write(handleClass(true, inputStream, null, null))
                    } else {
                        jarOutputStream.write(IOUtils.toByteArray(inputStream))
                    }
                    jarOutputStream.closeEntry()

                    byte[] stringFogBytes = createStringFogClass()
                    if (stringFogBytes != null) {
                        println "_qgllog  add StringFog.class"
                        ZipEntry stringFog = new ZipEntry("com/StringFog.class")
                        jarOutputStream.putNextEntry(stringFog)
                        jarOutputStream.write(stringFogBytes)
                        jarOutputStream.closeEntry()
                    }

                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                    jarOutputStream.closeEntry()
                }


            }
            // 结束
            jarOutputStream.close()
            jarFile.close()

            // 处理完输入文件之后，要把输出给下一个任务
            def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }

    private void handleDirectoryInput(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        println "_qgllog classesDirctory=" + directoryInput.file.getAbsolutePath()
        if (directoryInput.file.isDirectory()) {

            // 遍历目录的所有文件(包含子文件夹 子文件夹内的文件)
            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name;
                if (name.endsWith(".class") &&
                        !name.startsWith("R\$") &&
                        !name.equals("R.class") &&
                        !name.equals("BuildConfig.class")) {

                    // build/intermediates/javac/release/classes目录
                    String classesDirctory = directoryInput.file.getAbsolutePath()

                    FileInputStream inputStream = new FileInputStream(file)

                    // 把.class去掉  然后把\替换成.  然后把后面的.class去掉
                    String className = file.getAbsolutePath().substring(classesDirctory.length() + 1, file.getAbsolutePath().length() - 6).replace("\\", ".")

                    if (isCanFog(className)) {
                        println "_qgllog deal with dirctory class file " + className
                        handleClass(false, inputStream, className, classesDirctory)
                    }
                    inputStream.close()
                }
            }
        }

        //处理完输入文件之后，要把输出给下一个任务
        def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    byte[] handleClass(boolean isJar, InputStream inputStream, String className, String classDirctory) {

        ClassReader classReader = new ClassReader(inputStream)
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM8, classWriter) {
            @Override
            FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if (descriptor.equals("Ljava/lang/String;")) {
                    // 类中字符串的字段的值都设置为null
                    return super.visitField(access, name, descriptor, signature, null);
                }
                return super.visitField(access, name, descriptor, signature, value)
            }

            @Override
            MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions)
                mv = new MethodVisitor(Opcodes.ASM8, mv) {
                    @Override
                    void visitLdcInsn(Object value) {
                        if (value instanceof String) {
                            byte[] result = StringFog.encrypt(((String) value).getBytes());
                            if (result.length > 128) {
                                super.visitIntInsn(SIPUSH, result.length);
                            } else {
                                super.visitIntInsn(BIPUSH, result.length);
                            }

                            super.visitIntInsn(NEWARRAY, T_BYTE);
                            for (int i = 0; i < result.length; i++) {
                                super.visitInsn(DUP);
                                if (i == 0) {
                                    super.visitInsn(ICONST_0);
                                } else if (i == 1) {
                                    super.visitInsn(ICONST_1);
                                } else if (i == 2) {
                                    super.visitInsn(ICONST_2);
                                } else if (i == 3) {
                                    super.visitInsn(ICONST_3);
                                } else if (i == 4) {
                                    super.visitInsn(ICONST_4);
                                } else if (i == 5) {
                                    super.visitInsn(ICONST_5);
                                } else {
                                    if (i >= 128) {
                                        super.visitIntInsn(SIPUSH, i);
                                    } else {
                                        super.visitIntInsn(BIPUSH, i);
                                    }
                                }
                                super.visitIntInsn(BIPUSH, result[i]);
                                super.visitInsn(BASTORE);
                            }
                            super.visitMethodInsn(INVOKESTATIC, "com.StringFog", "decrypt", "([B)Ljava/lang/String;", false);
                        } else {
                            super.visitLdcInsn(value)
                        }
                    }
                }
                return mv
            }
        }

        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG)

        if (isJar) {
            return classWriter.toByteArray()
        } else {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(classDirctory, className.replace(".", "\\") + ".class"));
            fileOutputStream.write(classWriter.toByteArray());
            fileOutputStream.close();
            return null
        }
    }


    boolean isCanFog(String className) {
        try {
            Config config = project.extensions.findByType(Config.class)
            if (config.enable) {
                String[] pkgs = (config.pkgs + ";").split(";")
                for (int i = 0; i < pkgs.length; i++) {
                    String pkg = pkgs[i]
                    if (pkg != null && pkg != "" && className.startsWith(pkgs[i])) {
                        println "isCanFog=true className=" + className
                        return true
                    }
                }
            }
            return false
        } catch (Throwable throwable) {
            println "isCanFog err msg=" + throwable.getMessage()
            return false
        }
    }


    boolean isCreate = false;

    byte[] createStringFogClass() {
        if (isCreate) {
            return null
        }

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "com/StringFog", null, "java/lang/Object", null);

        cw.visitSource("StringFog.java", null);

        cw.visitInnerClass("java/util/Base64\$Decoder", "java/util/Base64", "Decoder", ACC_PUBLIC + ACC_STATIC);

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitLineNumber(3, l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "Lcom/StringFog;", null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "decrypt", "([B)Ljava/lang/String;", null, null);

        mv.visitCode();
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitLineNumber(5, l10);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64\$Decoder;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64\$Decoder", "decode", "(Ljava/lang/String;)[B", false);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([B)V", false);
        mv.visitInsn(ARETURN);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitLocalVariable("src", "[B", null, l10, l11, 0);
        mv.visitMaxs(6, 1);
        mv.visitEnd();
        cw.visitEnd();

        byte[] data = cw.toByteArray();
        isCreate = true
        return data
    }
}