package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
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
                each<RealizedNativeExecutable> {
                    derive { executable, app ->
                        println("-> EXECUTABLE $executable FOR $app")
                        val name = when (executable.buildType) {
                            BuildType.Debug -> executable.buildType.name
                            BuildType.Release -> "unsignedRelease"
                        }
                        executable.executable.entryPoint = generatedEntryPoint
                        val dist = app.distributionContainer.add(
                            name,
                            executable.buildType == BuildType.Debug,
                            false,
                            HostMachine.current.canBuild(executable.machine),
                            executable.machine,
                            executable.buildType,
                            DefaultNativeUiAppDistribution::class.java
                        )
                        dist.launcherFile.set(executable.binaryFile)
                        registerSibling(dist)
                    }
                }
            }

            applications.withApp<DefaultNativeUiApplication> { app ->
                app.entryPoint.convention("main")

                multiplatformComponents.macOS()

                for (machine in listOf(NativeMachine.MacOSArm64)) {
                    val generatorTask = tasks.register("nativeLauncher${machine.kotlinTarget}", NativeLauncher::class.java) {
                        it.sourceDirectory.set(layout.buildDirectory.dir("generated-main/${machine.kotlinTarget}"))
                        it.entryPoint.set(generatedEntryPoint)
                        it.delegateMethod.set(app.entryPoint)
                    }
                    project.kotlin.targets.getByName(machine.kotlinTarget).compilations.getByName("main").defaultSourceSet.kotlin.srcDir(generatorTask.flatMap { it.sourceDirectory })
                }
                app.macOS {
                    dependencies {
                        implementation("net.rubygrapefruit.plugins:native-launcher:1.0-dev")
                    }
                }
            }

            val app = extensions.create("application", DefaultNativeUiApplication::class.java, componentRegistry.factory)
            applications.register(app)
        }
    }
}