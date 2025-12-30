package net.rubygrapefruit.plugins.internal

import java.nio.file.Path
import kotlin.io.path.absolutePathString

sealed interface CliAppInvocation {
    /**
     * The file that is used to run the app.
     */
    val launcher: Path

    val commandLine: List<String>

    val expectedOutput: String?
}

class ScriptInvocation(
    val script: Path,
    val args: List<String>,
    val jvmVersion: Int?,
    override val expectedOutput: String?
) : CliAppInvocation {
    override val launcher: Path
        get() = script

    override val commandLine: List<String>
        get() = listOf(script.absolutePathString()) + args
}

class BinaryInvocation(
    val binary: Path,
    val args: List<String>,
    override val expectedOutput: String?
) : CliAppInvocation {
    override val launcher: Path
        get() = binary

    override val commandLine: List<String>
        get() = listOf(binary.absolutePathString()) + args
}
