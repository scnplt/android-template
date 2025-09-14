import dev.sertan.buildlogic.getLibrary
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.attributes.Bundling
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin

internal class AnalysisPlugin : Plugin<Project> {

    private val Project.reportsFolder
        get() = "$rootDir/reports"

    override fun apply(target: Project) {
        configureDetekt(target)
        configureKtlint(target)
    }

    private fun configureDetekt(project: Project): Unit = with(project) {
        val detekt = configurations.create("detekt")

        dependencies {
            add("detekt", getLibrary("detekt.cli"))
        }

        tasks.register<JavaExec>("detektCheck") {
            val outputFileWithoutExtension = "$reportsFolder/detekt"
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Run static code analysis with Detekt"
            classpath = detekt
            mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
            args(
                "-i", projectDir.absolutePath,
                "-c", "$rootDir/detekt.yml",
                "-r", "html:$outputFileWithoutExtension.html",
                "-r", "md:$outputFileWithoutExtension.md",
                "--build-upon-default-config",
                "--parallel"
            )
        }
    }

    private fun configureKtlint(project: Project): Unit = with(project) {
        val ktlint = configurations.create("ktlint")

        dependencies {
            addProvider(
                "ktlint",
                getLibrary("ktlint.cli"),
                Action<ExternalModuleDependency> {
                    attributes {
                        attribute(
                            Bundling.BUNDLING_ATTRIBUTE,
                            objects.named(Bundling::class, Bundling.EXTERNAL)
                        )
                    }
                }
            )
        }

        tasks.register<JavaExec>("ktlintCheck") {
            val outputFileWithoutExtension = "$reportsFolder/ktlint"
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Check Kotlin code style with Ktlint"
            classpath = ktlint
            mainClass.set("com.pinterest.ktlint.Main")
            args(
                "--reporter=html,output=$outputFileWithoutExtension.html",
                "--reporter=plain,output=$outputFileWithoutExtension.txt",
                "src/**/*.kt",
                "**.kts",
                "!**/build/**"
            )
        }

        tasks.register<JavaExec>("ktlintFormat") {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Check Kotlin code format with Ktlint"
            classpath = ktlint
            mainClass.set("com.pinterest.ktlint.Main")
            args(
                "--format",
                "**/src/**/*.kt",
                "**.kts",
                "!**/build/**"
            )
        }
    }
}
