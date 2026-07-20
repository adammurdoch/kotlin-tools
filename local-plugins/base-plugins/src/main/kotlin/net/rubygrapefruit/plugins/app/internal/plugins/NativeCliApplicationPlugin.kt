package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
open class NativeCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(CliApplicationBasePlugin::class.java)
            plugins.apply(ComponentBasePlugin::class.java)
            plugins.apply(MultiPlatformComponentBasePlugin::class.java)
            plugins.apply(MultiPlatformAppBasePlugin::class.java)

            componentRegistry.each<DefaultNativeCliApplication> {
                initialize { app ->
                    app.entryPoint.convention("main")
                }

                each<RealizedNativeExecutable> {
                    derive { executable, app ->
                        val buildType = executable.buildType
                        val machine = executable.machine

                        executable.executable.entryPoint = app.entryPoint.get()

                        val distribution = app.distributionContainer.add(
                            buildType.name,
                            buildType == BuildType.Debug,
                            buildType == BuildType.Release,
                            executable.canBuildOnHost,
                            machine,
                            buildType,
                            DefaultHasLauncherExecutableDistribution::class.java
                        )
                        distribution.launcherFilePath.set(app.appName.map { HostMachine.of(machine).exeName(it) })
                        distribution.launcherFile.set(executable.binaryFile)
                        registerSibling(distribution)
                    }
                }
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents, componentRegistry.factory)
            componentRegistry.register(app as DefaultNativeCliApplication)
        }
    }
}
