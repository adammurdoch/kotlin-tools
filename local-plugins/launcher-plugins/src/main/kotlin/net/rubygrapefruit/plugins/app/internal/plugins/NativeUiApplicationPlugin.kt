package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
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
                    val dist = app.distributionContainer.add(
                        name,
                        buildType == BuildType.Debug,
                        false,
                        HostMachine.current.canBuild(machine),
                        machine,
                        buildType,
                        DefaultNativeUiAppDistribution::class.java
                    )
                    dist.launcherFile.set(binaryFile)
                    executable.entryPoint = generatedEntryPoint
                }

                for (machine in listOf(NativeMachine.MacOSArm64, NativeMachine.MacOSX64)) {
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

            val app = extensions.create("application", DefaultNativeUiApplication::class.java, multiplatformComponents)
            applications.register(app)
        }
    }
}