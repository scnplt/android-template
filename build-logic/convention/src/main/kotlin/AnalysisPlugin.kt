import dev.sertan.buildlogic.getLibrary
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.attributes.Bundling
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * Directories that hold Kotlin but are not this project's source.
 *
 * Both tools are pointed at the root directory, and a filesystem walk does not read
 * `.gitignore` — so anything parked inside the repo gets analysed. `.claude/worktrees`
 * is the one that bites: a git worktree created there contains a full second copy of
 * the tree, and its files failed `ktlintCheck` and `detektCheck` in a checkout whose
 * own sources were clean. Because `scripts/git-hooks/pre-push` runs these same tasks,
 * that also blocked pushing. The reported path made the cause findable; a stale copy
 * of an old branch would not.
 */
private val NON_SOURCE_DIRECTORIES = listOf(
    "**/build/**",
    "**/generated/**",
    "**/.claude/**",
    "**/.gradle/**",
    "**/.git/**"
)

internal class AnalysisPlugin : Plugin<Project> {

    private val Project.reportsFolder
        get() = "$rootDir/reports"

    /**
     * Every file the tools are asked to read, as Gradle sees it.
     *
     * Declaring this as a task input is what lets the tasks be up-to-date and cacheable.
     * They are plain [JavaExec] registrations that declared neither inputs nor outputs, so
     * they re-ran in full on every invocation — the only tasks in a build that otherwise
     * sets `org.gradle.caching` and `org.gradle.configuration-cache` project-wide, and the
     * whole of CI's `lint` job, every run.
     */
    private fun Project.analysedSources(): FileTree = fileTree(rootDir) {
        include("**/src/**/*.kt", "**/*.kts")
        exclude(NON_SOURCE_DIRECTORIES)
    }

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
            val configFile = file("$rootDir/detekt.yml")
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Run static code analysis with Detekt"
            classpath = detekt
            mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")

            inputs.files(analysedSources())
                .withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName("sources")
            inputs.file(configFile)
                .withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName("detektConfig")
            outputs.files("$outputFileWithoutExtension.html", "$outputFileWithoutExtension.md")
                .withPropertyName("reports")
            outputs.cacheIf { true }

            args(
                "-i", rootDir.absolutePath,
                "-c", configFile.absolutePath,
                "-r", "html:$outputFileWithoutExtension.html",
                "-r", "md:$outputFileWithoutExtension.md",
                "--build-upon-default-config",
                "--parallel",
                "-ex", NON_SOURCE_DIRECTORIES.joinToString(",")
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

        // Same globs as ktlintFormat below. A root-relative "src/**/*.kt" and a "**.kts"
        // that only matches on some platforms made this task pass locally while CI failed
        // on the very files it had skipped.
        val patterns = listOf("**/src/**/*.kt", "**/*.kts") +
            NON_SOURCE_DIRECTORIES.map { "!$it" }

        tasks.register<JavaExec>("ktlintCheck") {
            val outputFileWithoutExtension = "$reportsFolder/ktlint"
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Check Kotlin code style with Ktlint"
            classpath = ktlint
            mainClass.set("com.pinterest.ktlint.Main")

            inputs.files(analysedSources())
                .withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName("sources")
            outputs.files("$outputFileWithoutExtension.html", "$outputFileWithoutExtension.txt")
                .withPropertyName("reports")
            outputs.cacheIf { true }

            args(
                listOf(
                    "--reporter=html,output=$outputFileWithoutExtension.html",
                    "--reporter=plain,output=$outputFileWithoutExtension.txt"
                ) + patterns
            )
        }

        // Deliberately no inputs/outputs: this one rewrites the files it reads, so an
        // up-to-date check would let a formatting fix be skipped after the sources it
        // would have fixed changed back.
        tasks.register<JavaExec>("ktlintFormat") {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Check Kotlin code format with Ktlint"
            classpath = ktlint
            mainClass.set("com.pinterest.ktlint.Main")
            args(listOf("--format") + patterns)
        }
    }
}
