package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.NativeLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project

class NativeUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(UiApplicationBasePlugin::class.java)
            applications.withApp<DefaultNativeUiApplication> { app ->
                app.entryPoint.convention("main")

                multiplatformComponents.macOS {
                    executable { }
                }

                val generatedEntryPoint = "uiMain"

                multiplatformComponents.eachNativeExecutable { machine, buildType, binaryFile, executable ->
                    val name = when (buildType) {
                        BuildType.Debug -> buildType.name
                        BuildType.Release -> "unsignedRelease"
                    }
                    val thisMachine = HostMachine.current.canBeBuilt && HostMachine.current.machine == machine
                    val dist = app.distributionContainer.add(
                        name,
                        buildType == BuildType.Debug && thisMachine,
                        buildType == BuildType.Release && thisMachine,
                        HostMachine.current.canBuild(machine),
                        machine,
                        buildType,
                        DefaultNativeUiAppDistribution::class.java
                    )
                    dist.launcherFile.set(binaryFile)
                    executable.entryPoint = generatedEntryPoint
                }

                val generatorTask = tasks.register("nativeLauncher", NativeLauncher::class.java) {
                    it.sourceDirectory.set(layout.buildDirectory.dir("generated-main"))
                    it.entryPoint.set(generatedEntryPoint)
                    it.delegateMethod.set(app.entryPoint)
                }
                app.macOS {
                    dependencies {
                        implementation("net.rubygrapefruit.plugins:native-launcher:1.0-dev")
                    }
                    generatedSource.add(generatorTask.flatMap { it.sourceDirectory })
                }
            }

            val app = extensions.create("application", DefaultNativeUiApplication::class.java, multiplatformComponents)
            applications.register(app)
        }
    }
}