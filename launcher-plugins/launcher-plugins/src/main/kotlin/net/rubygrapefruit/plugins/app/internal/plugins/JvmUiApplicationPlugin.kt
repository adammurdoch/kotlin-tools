package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.DefaultJvmUiAppDistribution
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
                app.attach()

                // TODO - 'can build' flag is incorrect - it depends on the JVM to be embedded
                for (machine in listOf(NativeMachine.MacOSArm64, NativeMachine.MacOSX64)) {
                    val canBuild = HostMachine.current.canBeBuilt && HostMachine.current.machine == machine
                    app.distributionContainer.add(
                        "unsignedRelease",
                        canBuild,
                        canBuild,
                        machine,
                        BuildType.Release,
                        DefaultJvmUiAppDistribution::class.java
                    )
                }

                app.distributionContainer.eachOfType<DefaultJvmUiAppDistribution> {
                    val nativeBinary = configurations.create("nativeBinaries${targetMachine.name}") {
                        it.attributes.attribute(
                            Usage.USAGE_ATTRIBUTE,
                            objects.named(Usage::class.java, "native-binary-${targetMachine.kotlinTarget}")
                        )
                        it.isCanBeResolved = true
                        it.isCanBeConsumed = false
                    }
                    dependencies.add(nativeBinary.name, "net.rubygrapefruit.plugins:native-launcher:1.0-dev")

                    val launcherTask = tasks.register(taskName("nativeLauncher"), NativeUiLauncher::class.java) {
                        it.inputFile.set(layout.file(nativeBinary.elements.map { it.first().asFile }))
                        it.outputFile.set(layout.buildDirectory.file(buildDirName("native-launcher") + "/native-launcher.kexe"))
                    }

                    val configTask = tasks.register(taskName("launcherConf"), LauncherConf::class.java) {
                        it.configFile.set(layout.buildDirectory.file(buildDirName("launcher-config") + "/launcher.conf"))
                        it.applicationDisplayName.set(capitalizedAppName)
                        it.iconName.set(app.iconName)
                        it.javaCommand.set(javaLauncherPath)
                        it.module.set(app.module.name)
                        it.mainClass.set(app.mainClass)
                    }

                    launcherFile.set(launcherTask.flatMap { it.outputFile })
                    withImage {
                        includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
                    }
                }
            }

            val app = extensions.create("application", DefaultJvmUiApplication::class.java)
            applications.register(app)
        }
    }
}