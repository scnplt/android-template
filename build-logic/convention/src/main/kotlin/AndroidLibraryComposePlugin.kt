import com.android.build.api.dsl.LibraryExtension
import dev.sertan.buildlogic.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType

internal class AndroidLibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply<AndroidLibraryPlugin>()

        val libExtension = extensions.getByType<LibraryExtension>()
        configureCompose(libExtension)
    }
}
