package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.DefaultHasLauncherExecutableDistribution
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.MacOS
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

                val targets = when {
                    HostMachine.current is MacOS -> listOf(NativeMachine.MacOSX64, NativeMachine.MacOSArm64)
                    HostMachine.current.canBeBuilt -> listOf(HostMachine.current.machine)
                    else -> emptyList()
                }

                // NativeBinary task uses correct JVM architecture to build for host machine
                // Could instead be parameterized with target architecture
                for (machine in targets) {
                    val default = machine == HostMachine.current.machine
                    val dist = app.distributionContainer.add(
                        null,
                        default,
                        default,
                        machine,
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