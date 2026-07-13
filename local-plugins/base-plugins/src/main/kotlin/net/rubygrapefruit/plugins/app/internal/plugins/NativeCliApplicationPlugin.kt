package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.mpp.Executable
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

@Suppress("unused")
open class NativeCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(CliApplicationBasePlugin::class.java)
            plugins.apply(ComponentBasePlugin::class.java)
            plugins.apply(MultiPlatformComponentBasePlugin::class.java)

            componentRegistry.each<DefaultNativeCliApplication> {
                derive { app ->
                    app.distributionContainer.each {
                        register(this)
                    }
                }

                each<NativeTarget> {
                    derive { target, app ->
                        val binaries = target.target.binaries
                        binaries.executable()
                        for (executable in binaries.withType(Executable::class.java)) {
                            val binaryFile = project.layout.file(executable.linkTaskProvider.map { it.binary.outputFile })
                            val buildType = when (executable.buildType) {
                                NativeBuildType.DEBUG -> BuildType.Debug
                                NativeBuildType.RELEASE -> BuildType.Release
                            }
                            val machine = target.machine
                            app.targets.attachExecutable(machine, buildType, binaryFile)
                            app.targets.configureTarget(machine, buildType) {
                                launcherFilePath.set(app.appName.map { HostMachine.of(machine).exeName(it) })
                            }
                            executable.entryPoint = app.entryPoint.get()
                        }
                    }
                }
            }

            applications.withApp<DefaultNativeCliApplication> { app ->
                app.entryPoint.convention("main")
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents, componentRegistry.factory)
            applications.register(app as DefaultNativeCliApplication)
        }
    }
}
