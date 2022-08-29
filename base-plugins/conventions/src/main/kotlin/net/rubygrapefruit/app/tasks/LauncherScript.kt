package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class LauncherScript : DefaultTask() {
    @get:OutputFile
    abstract val scriptFile: RegularFileProperty

    @get:Input
    abstract val module: Property<String>

    @get:Input
    abstract val mainClass: Property<String>

    @get:Input
    abstract val modulePath: ListProperty<String>

    @get:Input
    @get:Optional
    abstract val javaCommand: Property<String>

    @TaskAction
    fun generate() {
        val scriptFile = scriptFile.get().asFile

        val javaCommand = if (javaCommand.isPresent) "\$BASE_DIR/${javaCommand.get()}" else "java"
        val modulePath = if (modulePath.get().isNotEmpty()) "--module-path \"${modulePath.get().joinToString(":") { "\$BASE_DIR/libs/$it" }}\"" else ""

        scriptFile.writeText(
            """#!/bin/bash

SOURCE=${'$'}{BASH_SOURCE[0]}
while [ -L "${'$'}SOURCE" ]; do
    DIR=${'$'}( cd -P "${'$'}( dirname "${'$'}SOURCE" )" >/dev/null 2>&1 && pwd )
    SOURCE=${'$'}(readlink "${'$'}SOURCE")
    [[ ${'$'}SOURCE != /* ]] && SOURCE=${'$'}DIR/${'$'}SOURCE
done
BASE_DIR=${'$'}( cd -P "${'$'}( dirname "${'$'}SOURCE" )" >/dev/null 2>&1 && pwd )

$javaCommand $modulePath --module ${module.get()}/${mainClass.get()} "$*"
"""
        )
    }
}