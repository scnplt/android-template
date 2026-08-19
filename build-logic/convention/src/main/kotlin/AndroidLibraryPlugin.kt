import com.android.build.api.dsl.LibraryExtension
import dev.sertan.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply(plugin = "com.android.library")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)

            defaultConfig {
                consumerProguardFiles("consumer-rules.pro")
            }

            buildTypes {
                release {
                    // A library is never minified on its own — R8 runs once, over the
                    // whole app. `proguardFiles` here named a `proguard-rules.pro` that
                    // no library module has; it stayed harmless only because nothing
                    // resolved it while minification was off, and would have failed the
                    // build outright the day someone turned it on. Rules a library needs
                    // its consumers to apply belong in `consumer-rules.pro` above.
                    isMinifyEnabled = false
                }
            }
        }
    }
}
