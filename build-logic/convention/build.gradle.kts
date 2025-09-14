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
        register("composeApplication") {
            id = libs.plugins.sc.compose.application.get().pluginId
            implementationClass = "AndroidComposeAppPlugin"
        }
        register("composeLibrary") {
            id = libs.plugins.sc.compose.library.get().pluginId
            implementationClass = "AndroidComposeLibPlugin"
        }
        register("hilt") {
            id = libs.plugins.sc.hilt.get().pluginId
            implementationClass = "HiltPlugin"
        }
    }
}
