package net.rubygrapefruit.process

import net.rubygrapefruit.io.stream.CollectingBuffer

interface ProcessBuilder {
    fun start(): Process<Unit>

    fun startAndCollectOutput(): Process<String>
}

internal class DefaultProcessBuilder(private val commandLine: List<String>) : ProcessBuilder {
    override fun start(): Process<Unit> {
        return ProcessWithNoResult(start(ProcessStartSpec(commandLine, false)))
    }

    override fun startAndCollectOutput(): Process<String> {
        return ProcessWithStringResult(start(ProcessStartSpec(commandLine, true)))
    }
}

internal abstract class AbstractProcess<T>(protected val control: ProcessControl) : Process<T> {
}

internal class ProcessWithNoResult(control: ProcessControl) : AbstractProcess<Unit>(control) {
    override fun waitFor() {
        control.waitFor()
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