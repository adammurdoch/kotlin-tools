package net.rubygrapefruit.process

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.file.Directory

internal class ProcessStartSpec(
    val commandLine: List<String>,
    val directory: Directory?,
    val collectStdout: Boolean,
    val receiveStdin: Boolean,
    val checkExitCode: Boolean
)

internal interface ProcessControl {
    val stdout: RawSource

    val stdin: RawSink

    fun waitFor(): Int
}

internal expect fun start(spec: ProcessStartSpec): ProcessControl
