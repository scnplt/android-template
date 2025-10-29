plugins {
    alias(libs.plugins.sc.analysis)

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

tasks.register<Copy>("installGitHooks") {
    from(File(rootProject.rootDir, "scripts/git-hooks")) {
        include("*")
        filePermissions { unix("755") }
    }
    into(File(rootProject.rootDir, ".git/hooks"))
}
