import androidx.room.gradle.RoomExtension
import dev.sertan.buildlogic.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal class RoomPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        apply(plugin = "com.google.devtools.ksp")
        apply(plugin = "androidx.room")

        extensions.getByType<RoomExtension>()
            .schemaDirectory("$projectDir/schemas")

        dependencies {
            "ksp"(getLibrary("room.compiler"))
            "implementation"(getLibrary("room.runtime"))
            "implementation"(getLibrary("room.ktx"))
        }
    }
}
