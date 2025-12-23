package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.internal.HostMachine
import net.rubygrapefruit.plugins.app.internal.Windows
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
            " --module-path \"${modulePath.joinToString(":") { "\$LIB_DIR/$it" }}\""
        } else {
            ""
        }

        val scriptFile = scriptFile.get().asFile.toPath()
        scriptFile.writeText(
            $$"""#!/bin/bash

SOURCE=${BASH_SOURCE[0]}
while [ -L "$SOURCE" ]; do
    DIR=$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )
    SOURCE=$(readlink "$SOURCE")
    [[ $SOURCE != /* ]] && SOURCE=$DIR/$SOURCE
done
BASE_DIR=$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )
LIB_DIR="$BASE_DIR/$$libsDirPath"

JAVA_ARGS=""
[[ ! -z "$JAVA_DEBUG" ]] && JAVA_ARGS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"

$$javaCommand$$modulePathArg $JAVA_ARGS --module $${module.get()}/$${mainClass.get()} "$@"
"""
        )
        if (HostMachine.current !is Windows) {
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