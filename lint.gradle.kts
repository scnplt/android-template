val detekt by configurations.creating
val ktlint by configurations.creating

dependencies {
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.7")
    ktlint("com.pinterest:ktlint:0.50.0") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

val reportsDir = "$rootDir/build/reports"
val outputPath = "$reportsDir/${project.name}"

tasks.register<JavaExec>("detekt") {
    finalizedBy("deleteEmptyReports")
    group = "verification"
    description = "Run static code analysis with Detekt"
    classpath = configurations.getByName("detekt")
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    args = listOf(
        "-i", projectDir.absolutePath,
        "--build-upon-default-config",
        "--parallel",
        "-c", "$rootDir/detekt.yml",
        "-r", "html:$outputPath-detekt.html"
    )
}

tasks.register<JavaExec>("ktlint") {
    finalizedBy("deleteEmptyReports")
    group = "verification"
    description = "Check Kotlin code style with Ktlint"
    classpath = configurations.getByName("ktlint")
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf(
        "--reporter=html,output=$outputPath-ktlint.html",
        "src/**/*.kt"
    )
}

tasks.register("deleteEmptyReports") {
    doFirst {
        val filesToDelete = fileTree(reportsDir)
            .matching { include("**/*.html") }
            .filter { file ->
                val fileContent = file.readText()
                fileContent.contains("Congratulations, no issues found!")
                        || fileContent.contains("Total: 0")
            }

        filesToDelete.forEach { file ->
            file.delete()
            println("Deleted empty report: ${file.path}")
        }
    }
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations with Ktlint"
    classpath = configurations.getByName("ktlint")
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf(
        "--format",
        "src/**/*.kt"
    )
}
