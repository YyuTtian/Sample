import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import javassist.bytecode.ClassFile
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ClassTransform extends Transform {

    def tag = "ClassTransform  "

    Project project
    ClassPool classPool

    ClassTransform(Project project) {
        this.project = project

        classPool = ClassPool.getDefault()

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

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //导入android.jar
        def androidJar = project.android.bootClasspath[0].toString()
        println(tag + " android.jar = " + androidJar)
        classPool.appendClassPath(androidJar)

        def outputProvider = transformInvocation.outputProvider

        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { dirInput ->
                println(tag + "dirInput adb file path = " + dirInput.file.absolutePath)
                handleDirectory(dirInput.file)

                def dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, dest)
            }

            input.jarInputs.each { jarInputs ->
                println(tag + "jarInputs adb file path = " + jarInputs.file.absolutePath)
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
        //导入classes目录
        classPool.appendClassPath(dir.absolutePath)

        if (dir.isDirectory()) {
            dir.eachFileRecurse { file ->
                try {
                    def filePath = file.absolutePath
                    println(tag + "handleDirectory file path = " + filePath)
                    if (canModify(filePath)) {
                        def inputStream = new FileInputStream(file)
                        def ctClass = modify(inputStream)
                        println(tag + "modify write to " + dir.absolutePath)
                        ctClass.writeFile(dir.absolutePath)
                        ctClass.detach()
                    }
                } catch (Throwable throwable) {
                    println(tag + "handleDirectory fail msg=" + throwable.message)
                }
            }
        }
    }

    private File handleJar(File jarFile) {
        //导入jar包
        classPool.appendClassPath(jarFile.absolutePath)

        def inputJarFile = new JarFile(jarFile)
        def enumeration = inputJarFile.entries()

        def outputJarFile = new File(jarFile.parentFile, "temp_" + jarFile.name)
        if (outputJarFile.exists()) outputJarFile.delete()

        def jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputJarFile)))

        while (enumeration.hasMoreElements()) {
            try {
                def inputJarEntry = enumeration.nextElement()
                def inputJarEntryName = inputJarEntry.name

                def outputJarEntry = new JarEntry(inputJarEntryName)
                jarOutputStream.putNextEntry(outputJarEntry)
                println(tag + "inputJarEntryName = " + inputJarEntryName)

                def inputStream = inputJarFile.getInputStream(inputJarEntry)
                if (canModify(inputJarEntryName)) {
                    def ctClass = modify(inputStream)
                    def byteCode = ctClass.toBytecode()
                    ctClass.detach()

                    jarOutputStream.write(byteCode)
                    jarOutputStream.flush()

                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                inputStream.close()
            } catch (Throwable throwable) {
                println(tag + "handleJar fail msg=" + throwable.message)
            }
        }

        inputJarFile.close()
        jarOutputStream.closeEntry()
        jarOutputStream.flush()
        jarOutputStream.close()

        return outputJarFile
    }


    private boolean canModify(String filePath) {
        if (filePath.endsWith("DeviceConfig.class")) {
            return true
        } else {
            return false
        }
    }

    private CtClass modify(InputStream inputStream) {
        def classFile = new ClassFile(new DataInputStream(new BufferedInputStream(inputStream)))
        println(tag + "modify " + classFile.name)
        def ctClass = classPool.get(classFile.name)
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }

        def method = ctClass.getDeclaredMethod("getSerial")
        method.setBody("{return \"\";}")

        def method2 = ctClass.getDeclaredMethod("getSerialNo")
        method2.setBody("{return \"\";}")

        return ctClass
    }
}