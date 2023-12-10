package net.rubygrapefruit.process

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.io.stream.ReadStream

internal class ProcessStartSpec(
    val commandLine: List<String>,
    val directory: Directory?,
    val collectStdout: Boolean,
    val checkExitCode: Boolean
)

internal interface ProcessControl {
    val stdout: ReadStream

    fun waitFor(): Int
}

internal expect fun start(spec: ProcessStartSpec): ProcessControl
