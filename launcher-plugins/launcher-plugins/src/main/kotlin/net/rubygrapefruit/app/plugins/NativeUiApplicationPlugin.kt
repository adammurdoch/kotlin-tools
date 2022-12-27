package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultNativeUiApplication
import net.rubygrapefruit.app.internal.applications
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class NativeUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)
            with(extensions.getByType(KotlinMultiplatformExtension::class.java)) {
                macosX64 {
                    binaries {
                        executable {
                        }
                    }
                }
                macosArm64 {
                    binaries {
                        executable {
                        }
                    }
                }
            }
            val app = extensions.create("application", DefaultNativeUiApplication::class.java)
            applications.register(app)
        }
    }
}