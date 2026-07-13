package net.rubygrapefruit.plugins.app.internal.plugins

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
                derive { app ->
                    app.distributionContainer.each {
                        register(this)
                    }
                }

                each<RealizedNativeExecutable> {
                    derive { executable, app ->
                        app.targets.attachExecutable(executable.machine, executable.buildType, executable.binaryFile)
                        app.targets.configureTarget(executable.machine, executable.buildType) {
                            launcherFilePath.set(app.appName.map { HostMachine.of(executable.machine).exeName(it) })
                        }
                        executable.executable.entryPoint = app.entryPoint.get()
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
