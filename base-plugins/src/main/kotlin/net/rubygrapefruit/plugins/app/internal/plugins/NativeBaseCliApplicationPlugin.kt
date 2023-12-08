package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

open class NativeBaseCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)

            applications.withApp<DefaultNativeCliApplication> { app ->
                afterEvaluate {
                    for (machine in multiplatformComponents.targetMachines) {
                        if (HostMachine.current.canBuild(machine)) {
                            val nativeTarget = kotlin.targets.getByName(machine.kotlinTarget) as KotlinNativeTarget
                            val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                            val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                            app.addOutputBinary(machine, binaryFile)
                        }
                    }
                }
                app.distribution.launcherFilePath.set(app.appName.map { HostMachine.current.exeName(it) })
                app.distribution.launcherFile.set(app.outputBinary)
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents)
            applications.register(app as DefaultNativeCliApplication)
        }
    }
}
