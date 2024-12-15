package dev.sertan.android.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

internal val Project.reportFolder
    get() = "$rootDir/reports"

internal fun Project.applyPlugins(vararg ids: String) {
    ids.forEach { pluginManager.apply(it) }
}

internal fun Project.getLib(alias: String): Provider<MinimalExternalModuleDependency> =
    extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findLibrary(alias).get()

internal fun DependencyHandlerScope.implementation(dependency: Any): Dependency? =
    add("implementation", dependency)
