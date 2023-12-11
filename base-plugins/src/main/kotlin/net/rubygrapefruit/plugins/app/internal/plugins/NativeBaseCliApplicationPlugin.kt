package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.internal.DefaultNativeCliApplication
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable

open class NativeBaseCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)

            applications.withApp<DefaultNativeCliApplication> { app ->
                multiplatformComponents.eachNativeTarget { machine, nativeTarget ->
                    val executable = nativeTarget.binaries.withType(Executable::class.java).first()
                    val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                    val exe = app.addOutputBinary(machine, binaryFile)
                    if (machine == HostMachine.current.machine) {
                        app.distribution.launcherFile.set(exe.outputBinary)
                    }
                }
                app.distribution.launcherFilePath.set(app.appName.map { HostMachine.current.exeName(it) })
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents)
            applications.register(app as DefaultNativeCliApplication)
        }
    }
}
