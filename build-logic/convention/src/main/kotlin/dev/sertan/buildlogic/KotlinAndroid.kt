package dev.sertan.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    apply(plugin = "org.jetbrains.kotlin.android")

    commonExtension.apply {
        compileSdk = ProjectConfigs.COMPILE_SDK

        defaultConfig {
            minSdk = ProjectConfigs.MIN_SDK
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = ProjectConfigs.JAVA_VERSION
            targetCompatibility = ProjectConfigs.JAVA_VERSION
        }

        lint {
            htmlOutput = file("$reportsFolder/lint-results.html")
            textOutput = file("$reportsFolder/lint-results.txt")
        }
    }

    extensions.configure<KotlinAndroidExtension> {
        compilerOptions {
            jvmTarget.set(ProjectConfigs.JVM_TARGET)
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
        }
    }

    dependencies {
        "implementation"(getLibrary("androidx.core.ktx"))
    }
}
