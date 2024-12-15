/*
 * Copyright 2023 Sertan Canpolat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dev.sertan.android.buildlogic.getLib
import dev.sertan.android.buildlogic.reportFolder
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
    override fun apply(target: Project): Unit = with(target) {
        configureDetekt()
        configureKtlint()
    }

    private fun Project.configureDetekt() {
        val detekt = configurations.create("detekt")

        dependencies {
            add("detekt", getLib("detekt.cli"))
        }

        tasks.register<JavaExec>("detektCheck") {
            val outputFileWithoutExtension = "$reportFolder/detekt"
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            description = "Run static code analysis with Detekt"
            classpath = detekt
            mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
            args(
                "-i", projectDir.absolutePath,
                "-c", "$rootDir/.config/detekt.yml",
                "-r", "html:$outputFileWithoutExtension.html",
                "-r", "md:$outputFileWithoutExtension.md",
                "--build-upon-default-config",
                "--parallel"
            )
        }
    }

    private fun Project.configureKtlint() {
        val ktlint = configurations.create("ktlint")

        dependencies {
            addProvider(
                "ktlint",
                getLib("ktlint.cli"),
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
            val outputFileWithoutExtension = "$reportFolder/ktlint"
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
