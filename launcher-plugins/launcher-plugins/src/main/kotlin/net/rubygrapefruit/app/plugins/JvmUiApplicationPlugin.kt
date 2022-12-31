package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultJvmUiApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.LauncherConf
import net.rubygrapefruit.app.tasks.NativeUiLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project

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
                    it.javaCommand.set(app.distribution.javaLauncherPath)
                    it.module.set(app.module.name)
                    it.mainClass.set(app.mainClass)
                }
                val launcherTask = tasks.register("nativeLauncher", NativeUiLauncher::class.java) {
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