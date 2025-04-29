package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.internal.applications
import net.rubygrapefruit.plugins.app.internal.tasks.Install
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

open class CliApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<Application> { app ->
                tasks.register("install", Install::class.java) { task ->
                    task.description = "Installs the application"
                    task.sourceImageDirectory.set(app.distribution.flatMap { it.outputs.imageDirectory })
                    task.sourceLauncher.set(app.distribution.flatMap { it.outputs.launcherFile })
                    val targetDirectory = File(System.getProperty("user.home"), "bin")
                    task.targetImageDirectory.fileProvider(app.appName.map { targetDirectory.resolve("images/${it}") })
                    task.targetLinkDirectory.set(targetDirectory.resolve("links"))
                }
            }
        }
    }
}