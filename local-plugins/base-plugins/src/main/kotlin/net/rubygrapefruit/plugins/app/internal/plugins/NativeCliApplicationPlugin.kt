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

            componentRegistry.each<DefaultNativeCliApplication> {
                derive { app ->
                    app.distributionContainer.each {
                        register(this)
                    }
                }
            }

            applications.withApp<DefaultNativeCliApplication> { app ->
                app.entryPoint.convention("main")
                multiplatformComponents.eachNativeExecutable { machine, buildType, binaryFile, executable ->
                    app.targets.attachExecutable(machine, buildType, binaryFile)
                    app.targets.configureTarget(machine, buildType) {
                        launcherFilePath.set(app.appName.map { HostMachine.of(machine).exeName(it) })
                    }
                    target.afterEvaluate {
                        executable.entryPoint = app.entryPoint.get()
                    }
                }
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents, componentRegistry.factory)
            applications.register(app as DefaultNativeCliApplication)
        }
    }
}
