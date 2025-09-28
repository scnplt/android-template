plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sc.application.compose)
    alias(libs.plugins.sc.hilt)
}

android {
    namespace = "dev.sertan.template"

    defaultConfig {
        applicationId = "dev.sertan.template"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlin.serialization.json)
    implementation(libs.navigation.compose)
}
