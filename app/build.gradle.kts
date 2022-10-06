plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = Configs.NAMESPACE
    compileSdk = Configs.COMPILE_SDK

    defaultConfig {
        applicationId = Configs.APPLICATION_ID
        minSdk = Configs.MIN_SDK
        targetSdk = Configs.TARGET_SDK
        versionCode = Configs.VERSION_CODE
        versionName = Configs.VERSION_NAME

        testInstrumentationRunner = Configs.TEST_INSTRUMENTATION_RUNNER
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = Configs.JVM_TARGET
    }
}

dependencies {
    implementation(Libs.coreKtx)
    implementation(Libs.appcompat)
    implementation(Libs.material)

    testImplementation(Libs.junit)

    androidTestImplementation(Libs.extJunit)
    androidTestImplementation(Libs.espressoCore)
}
