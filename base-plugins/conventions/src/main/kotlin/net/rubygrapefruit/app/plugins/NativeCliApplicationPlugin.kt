package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.NativeMachine
import net.rubygrapefruit.app.internal.DefaultNativeCliApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.internal.currentOs
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
                    mingwX64 {
                        binaries {
                            executable {
                            }
                        }
                    }
                    val commonMain = sourceSets.getByName("commonMain")
                    val unixMain = sourceSets.create("unixMain") {
                        it.dependsOn(commonMain)
                    }
                    val macOsMain = sourceSets.create("macosMain") {
                        it.dependsOn(unixMain)
                    }
                    sourceSets.getByName("macosX64Main") {
                        it.dependsOn(macOsMain)
                    }
                    sourceSets.getByName("macosArm64Main") {
                        it.dependsOn(macOsMain)
                    }
                    sourceSets.getByName("linuxX64Main") {
                        it.dependsOn(unixMain)
                    }
                    val unixTest = sourceSets.create("unixTest") {
                        it.dependsOn(unixMain)
                        it.dependsOn(sourceSets.getByName("commonTest"))
                    }
                    val macOsTest = sourceSets.create("macosTest") {
                        it.dependsOn(unixTest)
                    }
                    sourceSets.getByName("macosX64Test") {
                        it.dependsOn(macOsTest)
                    }
                    sourceSets.getByName("macosArm64Test") {
                        it.dependsOn(macOsTest)
                    }
                    sourceSets.getByName("linuxX64Test") {
                        it.dependsOn(unixTest)
                    }
                }

                val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
                for (machine in NativeMachine.values()) {
                    if (currentOs.canBuild(machine)) {
                        val nativeTarget = extension.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget
                        val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                        val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                        app.addOutputBinary(machine, binaryFile, currentOs.machine == machine)
                    }
                }
                app.distribution.launcherFilePath.set(app.appName.map { currentOs.exeName(it) })
                app.distribution.launcherFile.set(app.outputBinary)
            }

            val app = extensions.create("application", DefaultNativeCliApplication::class.java)
            applications.register(app, app.distribution)
        }
    }
}
