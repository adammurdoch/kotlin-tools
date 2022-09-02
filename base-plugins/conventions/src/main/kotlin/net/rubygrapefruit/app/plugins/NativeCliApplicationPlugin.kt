package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultNativeCliApplication
import net.rubygrapefruit.app.internal.applications
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

open class NativeCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultNativeCliApplication> { app ->
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

                val nativeTargetName = if (System.getProperty("os.name").contains("linux", true)) {
                    "linuxX64"
                } else {
                    "macosX64"
                }

                val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
                val nativeTarget = extension.targets.getByName(nativeTargetName) as KotlinNativeTarget
                val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                app.outputBinary.set(binaryFile)
                app.distribution.launcherFile.set(binaryFile)
            }

            val app = extensions.create("application", DefaultNativeCliApplication::class.java)
            applications.register(app, app.distribution)
        }
    }
}
