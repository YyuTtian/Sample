import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.api.BaseVariantOutput
import com.android.builder.model.SourceProvider
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class MainPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {


        if (true) {
            def android = project.extensions.findByType(AppExtension.class)
            android.registerTransform(new ClassTransform(project))
            return
        }

        project.extensions.create("Config", Config.class)

        PathUtil.projectDir = project.projectDir.absolutePath
        PathUtil.buildDir = project.buildDir.getAbsolutePath()
        def android = project.extensions.findByType(AppExtension.class)
        android.applicationVariants.all { ApkVariant apkVariant ->

            Config config = project.extensions.findByType(Config.class)
            if (config != null && config.enable) {
                android.registerTransform(new StringFogTransform(project))
            }

            apkVariant.preBuild.doLast {
                Mapping.maps.clear()
            }

            apkVariant.assemble.doLast {
                FileUtils.deleteDirectory(new File(PathUtil.getSelfPath()))
            }

            apkVariant.mergeResources.doLast {
                apkVariant.sourceSets.each { SourceProvider sourceProvider ->
                    sourceProvider.resDirectories.each { File file ->
                        if (file.exists()) {
                            // res目录
                            ProcessFlat.processRes(file)
                        }
                    }
                }

                apkVariant.mergeResources.outputs.files.each { File file ->
                    ProcessFlat.copyFlat(file)
                }
            }


            apkVariant.outputs.each { BaseVariantOutput output ->
                output.processManifest.doLast {
                    apkVariant.sourceSets.each { SourceProvider sourceProvider ->
                        // 处理自己的Manifest
                        ProcessManifest.processSrc(sourceProvider.manifestFile)
                    }

                    // 处理合并后的Manifest
                    String mergeManifestPath = output.processManifest.manifestOutputDirectory.properties.get("orNull").toString() + File.separator + "AndroidManifest.xml"
                    LogUtil.log("mergeManifestPath=" + mergeManifestPath)
                    ProcessManifest.processMerge(new File(mergeManifestPath))
                }

                output.processResources.doLast {
                    output.processResources.outputs.files.each { File file ->
                        // 往aapt_rules里面加混淆规则
                        ProcessAaptRules.processAaptRules(file)
                    }
                }
            }
        }
    }
}