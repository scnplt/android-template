package dev.sertan.android.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object ApplicationConfig {
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0.0"
    const val MIN_SDK = 30
    const val COMPILE_SDK = 34
    val JAVA_VERSION = JavaVersion.VERSION_11
    val JVM_TARGET = JvmTarget.JVM_11
}
