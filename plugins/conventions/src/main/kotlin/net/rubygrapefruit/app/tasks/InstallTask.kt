package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class InstallTask : DefaultTask() {
    @TaskAction
    fun install() {
    }
}