package net.rubygrapefruit.process

interface ProcessBuilder {
    fun commandLine(commandLine: List<String>)
}

internal class DefaultProcessBuilder : ProcessBuilder {
    private val commandLine = mutableListOf<String>()

    override fun commandLine(commandLine: List<String>) {
        this.commandLine.clear()
        this.commandLine.addAll(commandLine)
    }

    fun start(): Process {
        return start(ProcessStartSpec(commandLine))
    }
}

internal class ProcessStartSpec(val commandLine: List<String>)

internal expect fun start(spec: ProcessStartSpec): Process
