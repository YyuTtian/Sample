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
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

import static org.objectweb.asm.Opcodes.ACC_PUBLIC
import static org.objectweb.asm.Opcodes.ACC_STATIC
import static org.objectweb.asm.Opcodes.GETSTATIC
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import static org.objectweb.asm.Opcodes.RETURN

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
    private String pkg

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        // 获取gradle中配置的参数
        Params params = project.extensions.findByType(Params.class)
        L.i(tag + "enable=" + params.enable + "   pkg=" + params.pkg)
        enable = params.enable
        pkg = params.pkg

        def outputProvider = transformInvocation.outputProvider

        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
                L.i(tag + "dirInput file path = " + dirInput.file.absolutePath)
                handleDirectory(dirInput.file)

                def dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, dest)
            }

            input.jarInputs.each { jarInputs ->
                L.i(tag + "jarInputs file path = " + jarInputs.file.absolutePath)
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
                L.i(tag + "handleDirectory file path = " + filePath)
                if (file.isFile() && canModify(filePath)) {
                    def inputStream = new FileInputStream(file)
                    def bytes = modify(inputStream)
                    FileUtils.writeByteArrayToFile(new File(file.absolutePath), bytes)
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
            L.i(tag + "inputJarEntryName = " + inputJarEntryName)

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

        String filePath = srcFilePath.replace("\\", ".").replace("/", ".")
        if (filePath.contains("R\$") || filePath.contains("R.class") || filePath.contains("BuildConfig.class")) {
            return false
        } else {
            if (filePath.contains(pkg)) {
                return true
            }
            return false
        }
    }

    private byte[] modify(InputStream inputStream) {
        ClassReader classReader = new ClassReader(inputStream)
        ClassWriter classWriter = new ClassWriter(0)
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, classWriter) {

            @Override
            void visitEnd() {

                Random random = new Random()
                int iMax = random.nextInt(200) + 100
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
                super.visitEnd();
            }
        }
        classReader.accept(classVisitor, 0)

        return classWriter.toByteArray()
    }
}