plugins {
    id("com.android.application").version("7.3.0").apply(false)
    id("com.android.library").version("7.3.0").apply(false)
    id("org.jetbrains.kotlin.android").version(Versions.KOTLIN).apply(false)
    id(Plugins.detekt).version(Versions.DETEKT).apply(false)
}

allprojects {
    apply(plugin = Plugins.detekt)

    tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class) {
        basePath = projectDir.toString()
        debug = true
        parallel = true

        reports {
            md.required.set(false)
            sarif.required.set(false)
            txt.required.set(false)
            xml.required.set(false)
            html.required.set(true)
            html.outputLocation.set(file("build/reports/detekt.html"))
        }
    }

    tasks.whenTaskAdded { if (name == "preBuild") dependsOn("detekt") }
}
