package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.DefaultJvmUiApplication
import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherConf
import net.rubygrapefruit.plugins.app.internal.tasks.NativeUiLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage

class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            plugins.apply(EmbeddedJvmLauncherPlugin::class.java)
            plugins.apply(UiApplicationBasePlugin::class.java)

            applications.withApp<DefaultJvmUiApplication> { app ->
                val capitalizedAppName = app.capitalizedAppName

                val configTask = tasks.register("launcherConf", LauncherConf::class.java) {
                    it.configFile.set(layout.buildDirectory.file("app/launcher.conf"))
                    it.applicationDisplayName.set(capitalizedAppName)
                    it.iconName.set(app.iconName)
                    it.javaCommand.set(app.javaLauncherPath)
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                }

                // TODO - 'can build' flag is incorrect - it depends on the JVM to be embedded
                app.targets.add(NativeMachine.MacOSArm64, listOf(BuildType.Release), HostMachine.current.machine == NativeMachine.MacOSArm64)
                app.targets.add(NativeMachine.MacOSX64, listOf(BuildType.Release), HostMachine.current.machine == NativeMachine.MacOSX64)

                app.eachTarget { machine, dist ->
                    val nativeBinary = configurations.create("nativeBinaries${dist.name}") {
                        it.attributes.attribute(
                            Usage.USAGE_ATTRIBUTE,
                            objects.named(Usage::class.java, "native-binary-${machine.kotlinTarget}")
                        )
                        it.isCanBeResolved = true
                        it.isCanBeConsumed = false
                    }
                    dependencies.add(nativeBinary.name, "net.rubygrapefruit.plugins:native-launcher:1.0-dev")

                    val launcherTask = tasks.register("nativeLauncher${dist.name}", NativeUiLauncher::class.java) {
                        it.inputFile.set(layout.file(nativeBinary.elements.map { it.first().asFile }))
                        it.outputFile.set(layout.buildDirectory.file("app-${dist.name}/native-launcher.kexe"))
                    }

                    dist.launcherFile.set(launcherTask.flatMap { it.outputFile })
                    dist.distTask.configure { distImage ->
                        distImage.includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmUiApplication::class.java)
            applications.register(app)
        }
    }
}