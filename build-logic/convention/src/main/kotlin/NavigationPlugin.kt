import dev.sertan.android.buildlogic.applyPlugins
import dev.sertan.android.buildlogic.getLib
import dev.sertan.android.buildlogic.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class NavigationPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins("androidx.navigation.safeargs.kotlin")

        dependencies {
            implementation(getLib("navigation.fragment"))
            implementation(getLib("navigation.ui"))
        }
    }
}
