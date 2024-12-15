package dev.sertan.android.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureAndroidCommon(extension: CommonExtension<*, *, *, *, *, *>) {
    when (extension) {
        is ApplicationExtension -> extension
        is LibraryExtension -> extension
        else -> return
    }.apply {
        compileSdk = ApplicationConfig.COMPILE_SDK

        defaultConfig {
            minSdk = ApplicationConfig.MIN_SDK
        }

        compileOptions {
            sourceCompatibility = ApplicationConfig.JAVA_VERSION
            targetCompatibility = ApplicationConfig.JAVA_VERSION
        }

        configure<KotlinAndroidProjectExtension> {
            compilerOptions.jvmTarget = ApplicationConfig.JVM_TARGET
        }
    }
}
