import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

import static org.objectweb.asm.Opcodes.*

class ClassTransform extends Transform {

    def tag = "ClassTransform  "

    Project project

    ClassTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "ClassTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        // 处理的类型,这里是要处理class文件
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        //处理范围.这里是整个项目所有资源
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        // 是否支持增量
        return false
    }

    private boolean enable
    private List<String> pkgs = new ArrayList<>()

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        // 获取gradle中配置的参数
        Params params = project.extensions.findByType(Params.class)
        enable = params.enable
        if (params.pkg.contains(",")) {
            pkgs = params.pkg.split(",").toList()
        } else {
            pkgs.add(params.pkg)
        }

        L.i(tag + "enable=" + params.enable + "   pkg=" + params.pkg + " pkgs.size=" + pkgs.size())

        def outputProvider = transformInvocation.outputProvider

        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
                handleDirectory(dirInput.file)

                def dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, dest)
            }

            input.jarInputs.each { jarInputs ->
                def srcFile = handleJar(jarInputs.file)

                def jarName = jarInputs.name
                def md5 = DigestUtils.md5Hex(jarInputs.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                def dest = outputProvider.getContentLocation(md5 + jarName, jarInputs.contentTypes, jarInputs.scopes, Format.JAR)
                FileUtils.copyFile(srcFile, dest)
            }
        }
    }

    private void handleDirectory(File dir) {
        //build/intermediates/javac/release/compileReleaseJavaWithJavac/classes目录
        if (dir.isDirectory()) {
            dir.eachFileRecurse { file ->
                def filePath = file.absolutePath
                if (canModify(filePath)) {
                    println("canModify_1")
                    if (file.isFile()) {
                        println("canModify_2")
                        def inputStream = new FileInputStream(file)
                        def bytes = modify(inputStream)
                        FileUtils.writeByteArrayToFile(new File(file.absolutePath), bytes)
                    } else {
                        println("canModify_3 " + filePath)
                    }
                } else {
                    println("canModify_4")
                }
            }
        }
    }

    private File handleJar(File jarFile) {
        def inputJarFile = new JarFile(jarFile)
        def enumeration = inputJarFile.entries()

        def outputJarFile = new File(jarFile.parentFile, "temp_" + jarFile.name)
        if (outputJarFile.exists()) outputJarFile.delete()

        def jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputJarFile)))

        while (enumeration.hasMoreElements()) {
            def inputJarEntry = enumeration.nextElement()
            def inputJarEntryName = inputJarEntry.name

            def outputJarEntry = new JarEntry(inputJarEntryName)
            jarOutputStream.putNextEntry(outputJarEntry)

            def inputStream = inputJarFile.getInputStream(inputJarEntry)
            if (canModify(inputJarEntryName)) {
                jarOutputStream.write(modify(inputStream))
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
        }

        inputJarFile.close()
        jarOutputStream.closeEntry()
        jarOutputStream.flush()
        jarOutputStream.close()
        return outputJarFile
    }


    private boolean canModify(String srcFilePath) {
        if (!enable) {
            return false
        }

        String fileName = new File(srcFilePath)
        if (fileName.contains("\$")) {
            return false
        }

        if (!srcFilePath.endsWith(".class")) {
            return false
        }

        String filePath = srcFilePath.replace("\\", ".").replace("/", ".")
        if (filePath.contains("R\$") || filePath.contains("R.class") || filePath.contains("BuildConfig.class")) {
            return false
        } else {
            boolean isCan = false
            pkgs.forEach {
                if (filePath.contains(it)) {
                    L.i("can modify pkg=" + it + " filePath=" + filePath)
                    isCan = true
                }
            }
            return isCan
        }
    }

    private byte[] modify(InputStream inputStream) {
        L.i("enter modify ")

        Random random = new Random()

        ClassReader classReader = new ClassReader(inputStream)
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new ClassVisitor(ASM9, classWriter) {

            @Override
            MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

                if (name == "<init>" || name == "<clinit>") {
                    return super.visitMethod(access, name, descriptor, signature, exceptions)
                }

                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
                return new AdviceAdapter(ASM9, methodVisitor, access, name, descriptor) {
                    @Override
                    protected void onMethodEnter() {
                        super.onMethodEnter()
                        // 方法头部插入代码
                        int iMax = random.nextInt(10) + 3
                        for (int i = 0; i < iMax; i++) {
                            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                            mv.visitLdcInsn(RandomStr.getStr())
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
                        }
                    }

                    @Override
                    protected void onMethodExit(int opcode) {
                        super.onMethodExit(opcode)
                        // 方法尾部插入代码
                        int iMax = random.nextInt(10) + 3
                        for (int i = 0; i < iMax; i++) {
                            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                            mv.visitLdcInsn(RandomStr.getStr())
                            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
                        }
                    }
                }
            }

            @Override
            void visitEnd() {
                L.i("visitEnd")
                int iMax = random.nextInt(100) + 50
                int jMax = random.nextInt(50) + 50

                for (int i = 0; i < iMax; i++) {
                    MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "_____addCode" + i, "()V", null, null)
                    mv.visitCode()
                    for (int j = 0; j < jMax; j++) {
                        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                        mv.visitLdcInsn(RandomStr.getStr())
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
                    }
                    mv.visitInsn(RETURN)
                    mv.visitMaxs(2, 0)
                    mv.visitEnd()
                }
                super.visitEnd()
            }
        }
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)

        return classWriter.toByteArray()
    }
}