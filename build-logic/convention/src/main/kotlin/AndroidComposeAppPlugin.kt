import com.android.build.api.dsl.ApplicationExtension
import dev.sertan.buildlogic.ProjectConfigs
import dev.sertan.buildlogic.configureCompose
import dev.sertan.buildlogic.configureKotlinAndroid
import dev.sertan.buildlogic.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidComposeAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply(plugin = "com.android.application")

        extensions.configure<ApplicationExtension> {
            configureKotlinAndroid(this)
            configureCompose(this)

            defaultConfig.targetSdk = ProjectConfigs.TARGET_SDK
        }

        dependencies {
            // Integration with activities
            "implementation"(getLibrary("androidx.activity.compose"))
            // Integration with ViewModels
            "implementation"(getLibrary("androidx.lifecycle.viewmodel.compose"))
            // UI Tests
            "androidTestImplementation"(getLibrary("compose.ui.test.junit4"))
            "debugImplementation"(getLibrary("compose.ui.test.manifest"))
        }
    }
}
