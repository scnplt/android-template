import com.android.build.gradle.LibraryExtension
import dev.sertan.buildlogic.configureCompose
import dev.sertan.buildlogic.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

internal class AndroidComposeLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply(plugin = "com.android.library")

        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            configureCompose(this)
        }
    }
}
