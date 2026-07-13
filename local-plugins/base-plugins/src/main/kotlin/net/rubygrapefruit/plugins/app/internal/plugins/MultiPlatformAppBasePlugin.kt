package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.MutableMultiPlatformApplication
import net.rubygrapefruit.plugins.app.internal.RealizedNativeExecutable
import net.rubygrapefruit.plugins.app.internal.RealizedNativeTarget
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

class MultiPlatformAppBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            componentRegistry.each<MutableMultiPlatformApplication> {
                each<RealizedNativeTarget> {
                    derive { target, _ ->
                        val binaries = target.target.binaries
                        binaries.executable()
                        for (executable in binaries.withType(Executable::class.java)) {
                            val binaryFile = project.layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                            val buildType = when (executable.buildType) {
                                NativeBuildType.DEBUG -> BuildType.Debug
                                NativeBuildType.RELEASE -> BuildType.Release
                            }
                            val machine = target.machine
                            registerSibling(RealizedNativeExecutable(machine, buildType, executable, binaryFile))
                        }
                    }
                }
            }
        }
    }
}