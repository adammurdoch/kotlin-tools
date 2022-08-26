package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class LauncherScript : DefaultTask() {
    @get:OutputFile
    abstract val scriptFile: RegularFileProperty

    @get:Input
    abstract val mainClass: Property<String>

    @get:Input
    abstract val classPath: ListProperty<String>

    @TaskAction
    fun generate() {
        val scriptFile = scriptFile.get().asFile
        scriptFile.writeText(
            """#!/bin/sh
BASE_DIR=`dirname "$0"`
java -cp ${classPath.get().joinToString(":") { "\$BASE_DIR/libs/$it" }} ${mainClass.get()} "$*"
"""
        )
    }
}