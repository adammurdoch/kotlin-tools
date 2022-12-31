package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.DefaultUiApplication
import net.rubygrapefruit.app.internal.applications
import org.gradle.api.Plugin
import org.gradle.api.Project

class UiApplicationBasePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<DefaultUiApplication> { app ->
                app.distribution.launcherFilePath.set(app.capitalizedAppName.map { "MacOS/$it" })
                applications.applyToDistribution { dist ->
                    dist.imageDirectory.set(layout.buildDirectory.dir(app.capitalizedAppName.map { "debug/$it.app" }))
                    dist.rootDirPath.set("Contents")
                }
            }
        }
    }
}