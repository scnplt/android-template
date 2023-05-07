plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.safeargs) apply false
}

subprojects {
    apply {
        from(rootProject.file("lint.gradle.kts"))
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
