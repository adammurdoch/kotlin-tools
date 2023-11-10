package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.internal.DefaultJvmUiApplication
import net.rubygrapefruit.plugins.app.internal.tasks.LauncherConf
import net.rubygrapefruit.plugins.app.internal.tasks.NativeUiLauncher
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.currentOs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage

class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(JvmApplicationBasePlugin::class.java)
            plugins.apply(UiApplicationBasePlugin::class.java)
            plugins.apply(EmbeddedJvmLauncherPlugin::class.java)

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

                val host = currentOs.machine
                val nativeBinary = configurations.create("nativeBinaries${host.name}") {
                    it.attributes.attribute(
                        Usage.USAGE_ATTRIBUTE,
                        objects.named(Usage::class.java, "native-binary-${host.kotlinTarget}")
                    )
                    it.isCanBeResolved = true
                    it.isCanBeConsumed = false
                }
                dependencies.add(nativeBinary.name, "net.rubygrapefruit.plugins:native-launcher:1.0-dev")

                val launcherTask = tasks.register("nativeLauncher", NativeUiLauncher::class.java) {
                    it.inputFile.set(layout.file(nativeBinary.elements.map { it.first().asFile }))
                    it.outputFile.set(layout.buildDirectory.file("app/native-launcher.kexe"))
                }

                app.distribution.launcherFile.set(launcherTask.flatMap { it.outputFile })

                applications.applyToDistribution { dist ->
                    dist.includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
                }
            }

            val app = extensions.create("application", DefaultJvmUiApplication::class.java)
            applications.register(app)
        }
    }
}