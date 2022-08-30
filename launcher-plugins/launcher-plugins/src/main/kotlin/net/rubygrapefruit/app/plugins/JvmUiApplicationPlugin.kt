package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmCliApplication
import net.rubygrapefruit.app.tasks.InfoPlist
import net.rubygrapefruit.app.tasks.LauncherConf
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // TODO - split out a base plugin
            plugins.apply("net.rubygrapefruit.jvm.cli-app")
            plugins.apply("net.rubygrapefruit.jvm.embedded-jvm")

            val app = extensions.getByType(JvmCliApplication::class.java)

            val capitalizedAppName = app.appName.map { it.replaceFirstChar { it.uppercase() } }

            val infoPlistTask = tasks.register("infoPlist", InfoPlist::class.java) {
                it.plistFile.set(layout.buildDirectory.file("app/Info.plist"))
                it.bundleName.set(capitalizedAppName)
                it.bundleIdentifier.set(capitalizedAppName)
                it.executableName.set(capitalizedAppName)
            }
            val configTask = tasks.register("launcherConf", LauncherConf::class.java) {
                it.configFile.set(layout.buildDirectory.file("app/launcher.conf"))
                it.applicationDisplayName.set(capitalizedAppName)
                it.javaCommand.set(app.distribution.javaLauncherPath)
                it.module.set(app.module)
                it.mainClass.set(app.mainClass)
            }

            app.distribution.content.from(infoPlistTask.flatMap { it.plistFile })
            app.distribution.content.from(configTask.flatMap { it.configFile })
        }
    }
}