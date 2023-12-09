package net.rubygrapefruit.process

interface ProcessBuilder {
    fun start(): Process<Unit>

    fun startAndCollectOutput(): Process<String>
}

internal class DefaultProcessBuilder(private val commandLine: List<String>) : ProcessBuilder {
    override fun start(): Process<Unit> {
        return ProcessWithNoResult(start(ProcessStartSpec(commandLine)))
    }

    override fun startAndCollectOutput(): Process<String> {
        TODO("Not yet implemented")
    }
}

internal abstract class AbstractProcess<T>(private val control: ProcessControl) : Process<T> {
    override fun waitFor(): T {
        control.waitFor()
        return result()
    }

    protected abstract fun result(): T
}

internal class ProcessWithNoResult(control: ProcessControl) : AbstractProcess<Unit>(control) {
    override fun result() {
        return Unit
    }
}