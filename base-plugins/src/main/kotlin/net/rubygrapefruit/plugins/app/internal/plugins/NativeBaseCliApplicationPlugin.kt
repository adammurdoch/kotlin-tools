package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.internal.DefaultNativeCliApplication
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

open class NativeBaseCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)

            applications.withApp<DefaultNativeCliApplication> { app ->
                multiplatformComponents.eachNativeTarget { machine, nativeTarget ->
                    for (executable in nativeTarget.binaries.withType(Executable::class.java)) {
                        val binaryFile = layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                        val buildType = when (executable.buildType) {
                            NativeBuildType.DEBUG -> BuildType.Debug
                            NativeBuildType.RELEASE -> BuildType.Release
                        }
                        app.attachExecutable(machine, buildType, binaryFile)
                        app.configureTarget(machine, buildType) {
                            launcherFilePath.set(app.appName.map { HostMachine.of(machine).exeName(it) })
                        }
                    }
                }
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents)
            applications.register(app as DefaultNativeCliApplication)
        }
    }
}
