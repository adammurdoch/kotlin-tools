package net.rubygrapefruit.process

import net.rubygrapefruit.io.stream.ReadStream

internal class ProcessStartSpec(val commandLine: List<String>, val collectStdout: Boolean, val checkExitCode: Boolean)

internal interface ProcessControl {
    val stdout: ReadStream

    fun waitFor(): Int
}

internal expect fun start(spec: ProcessStartSpec): ProcessControl
