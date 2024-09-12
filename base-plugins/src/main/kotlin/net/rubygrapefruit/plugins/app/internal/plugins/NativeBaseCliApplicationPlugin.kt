package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.internal.DefaultNativeCliApplication
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

open class NativeBaseCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(ApplicationBasePlugin::class.java)

            applications.withApp<DefaultNativeCliApplication> { app ->
                multiplatformComponents.eachNativeTarget { machine, buildType, binaryFile ->
                    app.targets.attachExecutable(machine, buildType, binaryFile)
                    app.targets.configureTarget(machine, buildType) {
                        launcherFilePath.set(app.appName.map { HostMachine.of(machine).exeName(it) })
                    }
                }
            }

            val app = extensions.create(NativeApplication::class.java, "application", DefaultNativeCliApplication::class.java, multiplatformComponents)
            applications.register(app as DefaultNativeCliApplication)
        }
    }
}
