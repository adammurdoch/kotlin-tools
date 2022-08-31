package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.JvmCliApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.InfoPlist
import net.rubygrapefruit.app.tasks.LauncherConf
import net.rubygrapefruit.app.tasks.NativeUiLauncher
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmUiApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
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
            val launcherTask = tasks.register("nativeLauncher", NativeUiLauncher::class.java) {
                it.outputFile.set(layout.buildDirectory.file("app/native-launcher.kexe"))
            }

            app.distribution.launcherFilePath.set(capitalizedAppName.map { "MacOS/$it" })
            app.distribution.launcherFile.set(launcherTask.flatMap { it.outputFile })

            applications.applyToDistribution { t ->
                t.imageDirectory.set(layout.buildDirectory.dir(capitalizedAppName.map { "$it.app" }))
                t.rootDirPath.set("Contents")
                t.includeFile("Info.plist", infoPlistTask.flatMap { it.plistFile })
                t.includeFile("Resources/launcher.conf", configTask.flatMap { it.configFile })
            }
        }
    }
}