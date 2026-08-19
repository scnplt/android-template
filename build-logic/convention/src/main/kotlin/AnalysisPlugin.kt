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

// Both tools are pointed at the root directory, and a filesystem walk does not read
// `.gitignore` — so anything parked inside the repo gets analysed. `.claude/worktrees` is
// the one that bites: a git worktree created there holds a full second copy of the tree,
// and its files failed `ktlintCheck` and `detektCheck` in a checkout whose own sources were
// clean. Because `scripts/git-hooks/pre-push` runs these same tasks, that also blocked
// pushing.

/** Build output. It nests — every module has its own — so these patterns must float. */
private val NESTED_OUTPUT_DIRECTORIES = listOf("**/build/**", "**/generated/**")

/**
 * Tooling directories, which exist only at the repository root — so their patterns are
 * anchored there rather than being given a leading double-star.
 *
 * That distinction is not pedantry. A floating pattern is not a stricter version of an
 * anchored one here, it is a broken one: when the checkout being analysed *is* a git
 * worktree under the `.claude` directory, every file's path contains that segment, so the
 * tools matched nothing and both tasks reported success having read no source at all. A
 * check that cannot fail is worse than the false failures the exclusion was added to stop.
 */
private val ROOT_TOOLING_DIRECTORIES = listOf(".claude", ".gradle", ".git")

/**
 * The two tools resolve their exclusion globs differently, so the same intent has to be
 * spelled twice: ktlint matches relative to the working directory, while detekt matches
 * against each file's absolute path, where an unprefixed pattern silently excludes
 * nothing. Anchoring detekt's copy at [root] also gives a worktree the behaviour it needs
 * for free: inside one there is no nested `.claude` directory, so its own sources are read.
 */
private fun exclusionsFor(root: java.io.File, absolute: Boolean): List<String> {
    val prefix = if (absolute) "${root.invariantSeparatorsPath}/" else ""
    return NESTED_OUTPUT_DIRECTORIES + ROOT_TOOLING_DIRECTORIES.map { "$prefix$it/**" }
}

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
        exclude(exclusionsFor(rootDir, absolute = false))
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
                "-ex", exclusionsFor(rootDir, absolute = true).joinToString(",")
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
            exclusionsFor(rootDir, absolute = false).map { "!$it" }

        tasks.register<JavaExec>("ktlintCheck") {
            val outputFileWithoutExtension = "$reportsFolder/ktlint"
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Check Kotlin code style with Ktlint"
            classpath = ktlint
            mainClass.set("com.pinterest.ktlint.Main")

            // ktlint anchors its patterns at the enclosing git root rather than at the
            // working directory, and a linked worktree resolves that to the *parent*
            // repository. A checkout placed inside the excluded tooling directory
            // therefore excludes itself: ktlint matches no files, warns, and exits 0.
            // Detekt is unaffected — its exclusions are absolute and rebased on this
            // root — but leaving ktlint to report success having read nothing would be
            // the exact failure this whole exclusion was written to avoid, one direction
            // over. Refuse to run instead of pretending to.
            val enclosingToolingDirectory = ROOT_TOOLING_DIRECTORIES.firstOrNull {
                rootDir.invariantSeparatorsPath.contains("/$it/")
            }
            doFirst {
                check(enclosingToolingDirectory == null) {
                    "ktlintCheck cannot analyse a checkout located inside " +
                        "'$enclosingToolingDirectory': ktlint would resolve its exclusions " +
                        "against the parent repository and silently match no files. Run it " +
                        "from the main checkout, or move this one outside that directory."
                }
            }

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
