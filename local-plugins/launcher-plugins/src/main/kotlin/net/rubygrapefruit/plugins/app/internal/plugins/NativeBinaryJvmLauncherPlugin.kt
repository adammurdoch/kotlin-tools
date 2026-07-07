package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.NativeBinary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

class NativeBinaryJvmLauncherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)

            componentRegistry.from<DefaultJvmCliApplication> {
                prepare { app ->
                    app.distributionContainer.add(
                        null,
                        true,
                        true,
                        HostMachine.current.canBeBuilt,
                        HostMachine.current.machine,
                        BuildType.Release,
                        DefaultHasLauncherExecutableDistribution::class.java
                    )
                }
            }

            componentRegistry.from<MutableJvmApplication> {
                derive { app ->
                    val binaryTask = tasks.registering<NativeBinary>("nativeBinary") {
                        launcherFile.set(layout.buildDirectory.file("native-binary/launcher"))
                        module.set(app.module.name)
                        mainClass.set(app.mainClass)
                        javaVersion.set(app.targetJvmVersion)
                        modulePath.from(app.runtimeModulePath)
                    }
                    register(LauncherExecutable(binaryTask))
                }
                from<HasLauncherExecutable> {
                    require<LauncherExecutable> {
                        derive { dist, app, exe ->
                            dist.launcherFilePath.set(app.appName.map { appName -> HostMachine.current.exeName(appName) })
                            val projectDirectory = layout.projectDirectory
                            dist.launcherFile.set(exe.binaryTask.flatMap { task -> task.launcherFile.map { projectDirectory.file(HostMachine.current.exeName(it.asFile.absolutePath)) } })
                        }
                    }
                }
            }
        }
    }
}

class LauncherExecutable(val binaryTask: TaskProvider<NativeBinary>)
