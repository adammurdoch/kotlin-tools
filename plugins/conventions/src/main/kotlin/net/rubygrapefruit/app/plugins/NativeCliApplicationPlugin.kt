package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.CliApplication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

open class NativeCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            extensions.create("application", CliApplication::class.java)

            repositories.mavenCentral()

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
                linuxX64 {
                    binaries {
                        executable {
                        }
                    }
                }
                val nativeMain = sourceSets.create("nativeMain") {
                    it.dependsOn(sourceSets.getByName("commonMain"))
                }
                sourceSets.getByName("macosX64Main") {
                    it.dependsOn(nativeMain)
                }
                sourceSets.getByName("macosArm64Main") {
                    it.dependsOn(nativeMain)
                }
                sourceSets.getByName("linuxX64Main") {
                    it.dependsOn(nativeMain)
                }
                val nativeTest = sourceSets.create("nativeTest") {
                    it.dependsOn(sourceSets.getByName("nativeMain"))
                    it.dependsOn(sourceSets.getByName("commonTest"))
                }
                sourceSets.getByName("macosX64Test") {
                    it.dependsOn(nativeTest)
                }
                sourceSets.getByName("linuxX64Main") {
                    it.dependsOn(nativeTest)
                }
            }
        }
    }
}
