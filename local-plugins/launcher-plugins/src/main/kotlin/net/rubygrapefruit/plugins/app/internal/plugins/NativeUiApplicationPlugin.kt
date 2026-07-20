package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.NativeLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project

private const val generatedEntryPoint = "uiMain"

@Suppress("unused")
class NativeUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(UiApplicationBasePlugin::class.java)
            plugins.apply(ComponentBasePlugin::class.java)
            plugins.apply(MultiPlatformComponentBasePlugin::class.java)
            plugins.apply(MultiPlatformAppBasePlugin::class.java)

            componentRegistry.each<DefaultNativeUiApplication> {
                initialize { app ->
                    app.entryPoint.convention("main")
                    app.macOS()
                    multiplatformComponents.forOperatingSystem(OperatingSystem.MacOS)
                }

                each<RealizedNativeTarget> {
                    derive { target, app ->
                        val generatorTask = tasks.register("generate${target.machine.kotlinTarget}Launcher", NativeLauncher::class.java) {
                            it.sourceDirectory.set(layout.buildDirectory.dir("generated/ui-launcher/${target.machine.kotlinTarget}"))
                            it.entryPoint.set(generatedEntryPoint)
                            it.delegateMethod.set(app.entryPoint)
                        }
                        val sourceSet = target.target.compilations.getByName("main").defaultSourceSet
                        sourceSet.kotlin.srcDir(generatorTask.flatMap { it.sourceDirectory })
                        sourceSet.dependencies {
                            implementation("net.rubygrapefruit.plugins:native-launcher:1.0-dev")
                        }
                    }
                }

                each<RealizedNativeExecutable> {
                    derive { executable, app ->
                        val name = when (executable.buildType) {
                            BuildType.Debug -> executable.buildType.name
                            BuildType.Release -> "unsignedRelease"
                        }
                        executable.executable.entryPoint = generatedEntryPoint
                        val machine = executable.machine
                        val dist = app.distributionContainer.add(
                            name,
                            executable.buildType == BuildType.Debug,
                            false,
                            executable.canBuildOnHost,
                            machine,
                            executable.buildType,
                            DefaultNativeUiAppDistribution::class.java
                        )
                        dist.launcherFile.set(executable.binaryFile)
                        registerSibling(dist)
                    }
                }
            }

            val app = extensions.create("application", DefaultNativeUiApplication::class.java, componentRegistry.factory)
            componentRegistry.register(app)
        }
    }
}