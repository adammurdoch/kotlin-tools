package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

class MultiPlatformAppBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.each<MutableMultiPlatformApplication> {
                each<HasOsTarget> {
                    derive { component, app ->
                        for (machine in component.target.machines) {
                            val kotlinTarget = kotlin.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget
                            val canBuild = NativeMachine.current.canBuild(machine)
                            registerSibling(RealizedNativeMachineApplication(canBuild, machine, app.entryPoint, kotlinTarget))
                        }
                    }
                }
            }
            componentRegistry.each<RealizedNativeMachineApplication> {
                derive { target ->
                    val binaries = target.target.binaries
                    binaries.executable()
                    for (executable in binaries.withType(Executable::class.java)) {
                        val binaryFile = project.layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                        val buildType = when (executable.buildType) {
                            NativeBuildType.DEBUG -> BuildType.Debug
                            NativeBuildType.RELEASE -> BuildType.Release
                        }
                        val machine = target.machine
                        registerSibling(RealizedNativeExecutable(target.canBuildOnHost, machine, buildType, executable, target.entryPoint, binaryFile))
                    }
                }
            }
        }
    }
}