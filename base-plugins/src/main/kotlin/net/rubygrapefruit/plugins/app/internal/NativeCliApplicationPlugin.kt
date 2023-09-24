package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.NativeMachine
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

            multiplatformComponents.registerSourceSets(
                ComponentTargets(
                    null,
                    setOf(
                        NativeMachine.LinuxX64,
                        NativeMachine.MacOSX64,
                        NativeMachine.MacOSArm64,
                        NativeMachine.WindowsX64
                    )
                )
            )
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
            applications.register(app)
        }
    }
}
