import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class MainPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("Params", Params.class)
        def android = project.extensions.findByType(AppExtension.class)

//        android.applicationVariants.all { ApkVariant apkVariant ->
//            L.i("apkVariant.buildType.getName()=" + apkVariant.buildType.getName())
//
//            Params params = project.extensions.findByType(Params.class)
//            if (params != null && params.enable && apkVariant.buildType.getName() == "release") {
//
//
//            }
//        }

        android.registerTransform(new ClassTransform(project))
    }
}