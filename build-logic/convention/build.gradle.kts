plugins {
    `kotlin-dsl`
}

group = "dev.sertan.android.buildlogic"

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
            id = "sc.analysis"
            implementationClass = "AnalysisPlugin"
        }
        register("androidApplication") {
            id = "sc.android.application"
            implementationClass = "AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = "sc.android.library"
            implementationClass = "AndroidLibraryPlugin"
        }
        register("hilt") {
            id = "sc.hilt"
            implementationClass = "HiltPlugin"
        }
        register("navigation") {
            id = "sc.navigation"
            implementationClass = "NavigationPlugin"
        }
    }
}
