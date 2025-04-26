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
                    task.launcher.set(app.distribution.flatMap { it.outputs.launcherFile })
                    task.linkDir.set(File(System.getProperty("user.home"), "bin/links"))
                }
            }
        }
    }
}