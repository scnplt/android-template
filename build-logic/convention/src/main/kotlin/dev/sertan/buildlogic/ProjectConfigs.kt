package dev.sertan.buildlogic

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object ProjectConfigs {
    const val MIN_SDK = 30
    const val COMPILE_SDK = 36
    const val TARGET_SDK = 36
    val JAVA_VERSION = JavaVersion.VERSION_17
    val JVM_TARGET = JvmTarget.JVM_17
}
