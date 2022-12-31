package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultUiApplication
import net.rubygrapefruit.app.internal.applications
import net.rubygrapefruit.app.tasks.InfoPlist
import org.gradle.api.Plugin
import org.gradle.api.Project

class UiApplicationBasePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultUiApplication> { app ->
                val capitalizedAppName = app.capitalizedAppName

                val infoPlistTask = tasks.register("infoPlist", InfoPlist::class.java) {
                    it.plistFile.set(layout.buildDirectory.file("app/Info.plist"))
                    it.bundleName.set(capitalizedAppName)
                    it.bundleIdentifier.set(capitalizedAppName)
                    it.executableName.set(capitalizedAppName)
                    it.iconName.set(app.iconName)
                }

                app.distribution.launcherFilePath.set(capitalizedAppName.map { "MacOS/$it" })

                applications.applyToDistribution { dist ->
                    dist.imageDirectory.set(layout.buildDirectory.dir(capitalizedAppName.map { "debug/$it.app" }))
                    dist.rootDirPath.set("Contents")
                    dist.includeFile("Info.plist", infoPlistTask.flatMap { it.plistFile })
                }
            }
        }
    }
}