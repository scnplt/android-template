plugins {
    `kotlin-dsl`
}

group = "dev.sertan.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("analysis") {
            id = libs.plugins.sc.analysis.get().pluginId
            implementationClass = "AnalysisPlugin"
        }
        register("applicationCompose") {
            id = libs.plugins.sc.application.compose.get().pluginId
            implementationClass = "AndroidApplicationComposePlugin"
        }
        register("hilt") {
            id = libs.plugins.sc.hilt.get().pluginId
            implementationClass = "HiltPlugin"
        }
        register("library") {
            id = libs.plugins.sc.library.asProvider().get().pluginId
            implementationClass = "AndroidLibraryPlugin"
        }
        register("libraryCompose") {
            id = libs.plugins.sc.library.compose.get().pluginId
            implementationClass = "AndroidLibraryComposePlugin"
        }
    }
}
