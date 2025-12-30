package net.rubygrapefruit.plugins.internal

import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed interface CliAppInvocation {
    val launcher: Path

    val commandLine: List<String>
}

class ScriptInvocation(
    val script: Path,
    val args: List<String>,
    val jvmVersion: Int?
) : CliAppInvocation {
    override val launcher: Path
        get() = script

    override val commandLine: List<String>
        get() = listOf(script.absolutePathString()) + args
}

class BinaryInvocation(
    val binary: Path,
    val args: List<String>
) : CliAppInvocation {
    override val launcher: Path
        get() = binary

    override val commandLine: List<String>
        get() = listOf(binary.absolutePathString()) + args
}
