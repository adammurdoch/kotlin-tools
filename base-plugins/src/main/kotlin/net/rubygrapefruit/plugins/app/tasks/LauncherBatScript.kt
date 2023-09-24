package net.rubygrapefruit.plugins.app.tasks

import org.gradle.api.tasks.TaskAction
import kotlin.io.path.writeText

abstract class LauncherBatScript : LauncherScript() {
    @TaskAction
    fun generate() {
        val javaCommand = javaLauncherPath.map { "%BASE_DIR%/$it" }.getOrElse("java")
        val libsDirPath = libsDirPath.get()
        val modulePath = modulePath.get()
        val modulePathArg = if (modulePath.isNotEmpty()) {
            "--module-path \"${modulePath.joinToString(";") { "%BASE_DIR%/$libsDirPath/$it" }}\""
        } else {
            ""
        }

        val scriptFile = scriptFile.get().asFile.toPath()
        scriptFile.writeText(
            """@echo off
set BASE_DIR=%~dp0
$javaCommand $modulePathArg --module ${module.get()}/${mainClass.get()} %*            
"""
        )
    }
}