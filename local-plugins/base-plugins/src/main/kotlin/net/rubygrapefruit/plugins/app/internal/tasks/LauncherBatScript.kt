package net.rubygrapefruit.plugins.app.internal.tasks

import org.gradle.api.tasks.TaskAction
import kotlin.io.path.writeText

abstract class LauncherBatScript : LauncherScript() {
    @TaskAction
    fun generate() {
        val javaCommand = javaLauncherPath.map { "%BASE_DIR%/$it" }.getOrElse("java")
        val libsDirPath = libsDirPath.get()
        val modulePath = modulePath.get()
        val modulePathArg = if (modulePath.isNotEmpty()) {
            "--module-path \"${modulePath.joinToString(";") { "%LIB_DIR%\\$it" }}\""
        } else {
            ""
        }

        val scriptFile = scriptFile.get().asFile.toPath()
        scriptFile.writeText(
            """@echo off
set BASE_DIR=%~dp0
set LIB_DIR="%BASE_DIR%\\$libsDirPath"
$javaCommand $modulePathArg --module ${module.get()}/${mainClass.get()} %*            
""".replace("\n", "\r\n")
        )
    }
}