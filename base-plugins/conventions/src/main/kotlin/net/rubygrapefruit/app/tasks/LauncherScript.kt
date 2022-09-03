package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.writeText

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
    abstract val libsDirPath: Property<String>

    @get:Input
    @get:Optional
    abstract val javaLauncherPath: Property<String>

    @TaskAction
    fun generate() {
        val javaCommand = if (javaLauncherPath.isPresent) "\$BASE_DIR/${javaLauncherPath.get()}" else "java"
        val libsDirPath = libsDirPath.get()
        val modulePath = modulePath.get()
        val modulePathArg = if (modulePath.isNotEmpty()) {
            "--module-path \"${modulePath.joinToString(":") { "\$BASE_DIR/$libsDirPath/$it" }}\""
        } else {
            ""
        }

        val scriptFile = scriptFile.get().asFile.toPath()
        scriptFile.writeText(
            """#!/bin/bash

SOURCE=${'$'}{BASH_SOURCE[0]}
while [ -L "${'$'}SOURCE" ]; do
    DIR=${'$'}( cd -P "${'$'}( dirname "${'$'}SOURCE" )" >/dev/null 2>&1 && pwd )
    SOURCE=${'$'}(readlink "${'$'}SOURCE")
    [[ ${'$'}SOURCE != /* ]] && SOURCE=${'$'}DIR/${'$'}SOURCE
done
BASE_DIR=${'$'}( cd -P "${'$'}( dirname "${'$'}SOURCE" )" >/dev/null 2>&1 && pwd )

$javaCommand $modulePathArg --module ${module.get()}/${mainClass.get()} "$@"
"""
        )
        Files.setPosixFilePermissions(
            scriptFile,
            setOf(
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ,
                PosixFilePermission.OTHERS_EXECUTE
            )
        )
    }
}