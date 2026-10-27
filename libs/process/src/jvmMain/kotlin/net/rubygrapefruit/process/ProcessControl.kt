package net.rubygrapefruit.process

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import net.rubygrapefruit.file.file
import net.rubygrapefruit.io.IOException
import net.rubygrapefruit.io.stream.InputStreamBackedRawSource
import net.rubygrapefruit.io.stream.OutputStreamBackedRawSink
import java.lang.Process
import java.lang.ProcessBuilder.Redirect

internal actual fun start(spec: ProcessStartSpec): ProcessControl {
    val builder = ProcessBuilder(spec.commandLine)
    if (spec.directory != null) {
        builder.directory(spec.directory.file())
    }
    if (!spec.collectStdout) {
        builder.redirectOutput(Redirect.INHERIT)
    }
    if (!spec.receiveStdin) {
        builder.redirectInput(Redirect.INHERIT)
    }
    val process = builder.start()
    return JvmProcessControl(process, spec)
}

private class JvmProcessControl(val process: Process, val spec: ProcessStartSpec) : ProcessControl {
    override val stdout: RawSource
        get() = InputStreamBackedRawSource(ProcessSource, process.inputStream)

    override val stdin: RawSink
        get() = OutputStreamBackedRawSink(process.outputStream)

    override fun waitFor(): Int {
        val exitCode = process.waitFor()
        if (spec.checkExitCode && exitCode != 0) {
            throw IOException("Command failed with exit code $exitCode")
        }
        return exitCode
    }
}
