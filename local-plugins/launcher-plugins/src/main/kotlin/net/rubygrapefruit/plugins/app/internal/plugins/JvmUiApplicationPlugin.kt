package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.*
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherConf
import net.rubygrapefruit.plugins.app.internal.tasks.NativeUiLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage

@Suppress("unused")
class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            plugins.apply(EmbeddedJvmLauncherPlugin::class.java)
            plugins.apply(UiApplicationBasePlugin::class.java)

            componentRegistry.from<DefaultJvmUiApplication> {
                derive { app ->
                    val machine = NativeMachine.MacOSArm64
                    // TODO - 'can build' flag is incorrect - it depends on the JVM to be embedded
                    val canBuild = HostMachine.current.canBeBuilt && HostMachine.current.machine == machine
                    val dist = app.distributionContainer.add(
                        "unsignedRelease",
                        true,
                        false,
                        canBuild,
                        machine,
                        BuildType.Release,
                        DefaultJvmUiAppDistribution::class.java
                    )
                    register(dist)
                }

                from<DefaultJvmUiAppDistribution> {
                    derive { dist, app ->
                        val nativeBinary = configurations.create("nativeBinaries${dist.targetMachine.name}") {
                            it.attributes.attribute(
                                Usage.USAGE_ATTRIBUTE,
                                objects.named(Usage::class.java, "native-binary-${dist.targetMachine.kotlinTarget}")
                            )
                            it.isCanBeResolved = true
                            it.isCanBeConsumed = false
                        }
                        dependencies.add(nativeBinary.name, "net.rubygrapefruit.plugins:native-jvm-launcher:1.0-dev")

                        val launcherTask = tasks.register(dist.taskName("nativeLauncher"), NativeUiLauncher::class.java) {
                            it.inputFile.set(layout.file(nativeBinary.elements.map { it.first().asFile }))
                            it.outputFile.set(layout.buildDirectory.file(dist.buildDirName("native-launcher") + "/native-launcher.kexe"))
                        }

                        val configTask = tasks.register(dist.taskName("launcherConf"), LauncherConf::class.java) {
                            it.configFile.set(layout.buildDirectory.file(dist.buildDirName("launcher-config") + "/launcher.conf"))
                            it.applicationDisplayName.set(app.capitalizedAppName)
                            it.iconName.set(app.iconName)
                            it.javaCommand.set(dist.javaLauncherPath)
                            it.module.set(app.module.name)
                            it.mainClass.set(app.mainClass)
                        }

                        dist.launcherFile.set(launcherTask.flatMap { it.outputFile })
                        dist.withImage {
                            includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
                        }
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmUiApplication::class.java)
            applications.register(app)
        }
    }
}