import com.android.build.api.dsl.ApplicationExtension
import dev.sertan.android.buildlogic.ApplicationConfig
import dev.sertan.android.buildlogic.applyPlugins
import dev.sertan.android.buildlogic.configureAndroidCommon
import dev.sertan.android.buildlogic.getLib
import dev.sertan.android.buildlogic.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins(
            "com.android.application",
            "org.jetbrains.kotlin.android"
        )

        extensions.configure<ApplicationExtension> {
            configureAndroidCommon(this)

            defaultConfig {
                applicationId = namespace
                versionCode = ApplicationConfig.VERSION_CODE
                versionName = ApplicationConfig.VERSION_NAME
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    isShrinkResources = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }

            buildFeatures {
                viewBinding = true
            }
        }

        dependencies {
            implementation(getLib("core.ktx"))
            implementation(getLib("appcompat"))
            implementation(getLib("material"))
        }
    }
}
