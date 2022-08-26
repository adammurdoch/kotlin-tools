package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.CliApplication
import net.rubygrapefruit.app.tasks.InstallTask
import org.gradle.api.Project

internal abstract class ApplicationRegistry(private val project: Project) {
    fun register(app: CliApplication) {
        project.tasks.register("install", InstallTask::class.java) {
        }
    }
}