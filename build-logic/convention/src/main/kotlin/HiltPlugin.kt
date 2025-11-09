import dev.sertan.buildlogic.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

internal class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply(plugin = "com.google.devtools.ksp")
        apply(plugin = "com.google.dagger.hilt.android")

        dependencies {
            "ksp"(getLibrary("hilt.android.compiler"))
            "implementation"(getLibrary("hilt.android"))
        }
    }
}
