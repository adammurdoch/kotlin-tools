package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.DefaultNativeCliApplication
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
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

            multiplatformComponents.desktop {
                executable { }
            }

            applications.withApp<DefaultNativeCliApplication> { app ->
                val extension = extensions.getByType(KotlinMultiplatformExtension::class.java)
                for (machine in NativeMachine.values()) {
                    if (HostMachine.current.canBuild(machine)) {
                        val nativeTarget = extension.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget
                        val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                        val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                        app.addOutputBinary(machine, binaryFile, HostMachine.current.machine == machine)
                    }
                }
                app.distribution.launcherFilePath.set(app.appName.map { HostMachine.current.exeName(it) })
                app.distribution.launcherFile.set(app.outputBinary)
            }

            val app = extensions.create("application", DefaultNativeCliApplication::class.java)
            applications.register(app)
        }
    }
}
