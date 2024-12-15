import com.android.build.api.dsl.LibraryExtension
import dev.sertan.android.buildlogic.applyPlugins
import dev.sertan.android.buildlogic.configureAndroidCommon
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins(
            "com.android.library",
            "org.jetbrains.kotlin.android"
        )

        extensions.configure<LibraryExtension> {
            configureAndroidCommon(this)

            defaultConfig {
                consumerProguardFiles("consumer-rules.pro")
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
        }
    }
}
