package dev.sertan.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    apply(plugin = "org.jetbrains.kotlin.plugin.compose")

    commonExtension.buildFeatures.compose = true

    dependencies {
        val composeBom = platform(getLibrary("compose.bom"))
        "implementation"(composeBom)
        "androidTestImplementation"(composeBom)
        // Material 3
        "implementation"(getLibrary("compose.material3"))
        "implementation"(getLibrary("compose.material.icons.extended"))
        // Preview Support
        "implementation"(getLibrary("compose.ui.tooling.preview"))
        "debugImplementation"(getLibrary("compose.ui.tooling"))
    }
}
