package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.internal.Windows
import net.rubygrapefruit.app.internal.currentOs
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import kotlin.io.path.writeText

abstract class LauncherBashScript : LauncherScript() {
    @TaskAction
    fun generate() {
        val javaCommand = javaLauncherPath.map { "\$BASE_DIR/$it" }.getOrElse("java")
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
        if (currentOs() != Windows) {
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
}