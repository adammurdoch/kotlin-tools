package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.DefaultHasLauncherExecutableDistribution
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.MutableJvmApplication
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.registering
import net.rubygrapefruit.plugins.app.internal.tasks.NativeBinary
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeBinaryJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<MutableJvmApplication> { app ->
                val binaryTask = tasks.registering<NativeBinary>("nativeBinary") {
                    launcherFile.set(layout.buildDirectory.file("native-binary/launcher"))
                    module.set(app.module.name)
                    mainClass.set(app.mainClass)
                    javaVersion.set(app.targetJavaVersion)
                    modulePath.from(app.runtimeModulePath)
                }

                // NativeBinary task uses correct JVM architecture to build for host machine
                // Don't add the target if Kotlin cannot be built for this host.
                // Should instead add distribution for each target
                if (HostMachine.current.canBeBuilt) {
                    val dist = app.distributionContainer.add(
                        HostMachine.current.machine.kotlinTarget,
                        true,
                        true,
                        HostMachine.current.machine,
                        BuildType.Release,
                        DefaultHasLauncherExecutableDistribution::class.java
                    )
                    dist.launcherFilePath.set(app.appName.map { appName -> HostMachine.current.exeName(appName) })
                    dist.launcherFile.set(binaryTask.flatMap { task -> task.launcherFile.map { layout.projectDirectory.file(HostMachine.current.exeName(it.asFile.absolutePath)) } })
                }
            }
        }
    }
}