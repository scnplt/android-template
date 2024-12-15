import dev.sertan.android.buildlogic.applyPlugins
import dev.sertan.android.buildlogic.getLib
import dev.sertan.android.buildlogic.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

internal class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins(
            "kotlin-kapt",
            "com.google.dagger.hilt.android"
        )

        dependencies {
            implementation(getLib("hilt.android"))
            add("kapt", getLib("hilt.compiler"))
        }

        extensions.configure<KaptExtension> {
            correctErrorTypes = true
        }
    }
}
