package net.rubygrapefruit.process

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.io.stream.CollectingBuffer

interface ProcessBuilder {
    /**
     * Specifies the directory to start the process in. Defaults to the current directory of this process.
     */
    fun directory(dir: Directory): ProcessBuilder

    fun start(): Process<Unit>

    fun startAndCollectOutput(): Process<String>

    fun startAndGetExitCode(): Process<Int>
}

internal class DefaultProcessBuilder(private val commandLine: List<String>) : ProcessBuilder {
    private var dir: Directory? = null

    override fun directory(dir: Directory): ProcessBuilder {
        this.dir = dir
        return this
    }

    override fun start(): Process<Unit> {
        return ProcessWithNoResult(start(ProcessStartSpec(commandLine, dir, false, true)))
    }

    override fun startAndCollectOutput(): Process<String> {
        return ProcessWithStringResult(start(ProcessStartSpec(commandLine, dir, true, true)))
    }

    override fun startAndGetExitCode(): Process<Int> {
        return ProcessWithExitCodeResult(start(ProcessStartSpec(commandLine, dir, false, false)))
    }
}

internal abstract class AbstractProcess<T>(protected val control: ProcessControl) : Process<T> {
}

internal class ProcessWithNoResult(control: ProcessControl) : AbstractProcess<Unit>(control) {
    override fun waitFor() {
        control.waitFor()
    }
}

internal class ProcessWithExitCodeResult(control: ProcessControl) : AbstractProcess<Int>(control) {
    override fun waitFor(): Int {
        return control.waitFor()
    }
}

internal class ProcessWithStringResult(control: ProcessControl) : AbstractProcess<String>(control) {
    override fun waitFor(): String {
        val buffer = CollectingBuffer()
        buffer.readFrom(control.stdout)
        control.waitFor()
        return buffer.decodeToString()
    }
}