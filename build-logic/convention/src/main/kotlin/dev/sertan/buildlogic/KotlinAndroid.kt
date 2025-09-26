package dev.sertan.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    apply(plugin = "org.jetbrains.kotlin.android")

    commonExtension.apply {
        compileSdk = ProjectConfigs.COMPILE_SDK

        defaultConfig.minSdk = ProjectConfigs.MIN_SDK

        compileOptions {
            sourceCompatibility = ProjectConfigs.JAVA_VERSION
            targetCompatibility = ProjectConfigs.JAVA_VERSION
        }

        lint {
            htmlOutput = file("$reportsFolder/lint-results.html")
            textOutput = file("$reportsFolder/lint-results.txt")
        }
    }

    extensions.getByType<KotlinAndroidExtension>()
        .compilerOptions
        .jvmTarget.set(ProjectConfigs.JVM_TARGET)

    dependencies {
        "implementation"(getLibrary("androidx.core.ktx"))
    }
}
