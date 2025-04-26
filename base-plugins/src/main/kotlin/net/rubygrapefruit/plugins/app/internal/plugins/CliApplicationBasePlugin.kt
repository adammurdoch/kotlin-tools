package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.internal.applications
import org.gradle.api.Plugin
import org.gradle.api.Project

open class CliApplicationBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(ApplicationBasePlugin::class.java)
            applications.withApp<Application> { app ->
                tasks.register("install") { task ->
                    task.description = "Installs the application"
                    task.inputs.dir(app.distribution.flatMap { it.imageDirectory })
                    task.doLast {
                        println("Installed ${app.appName.get()}")
                    }
                }
            }
        }
    }
}