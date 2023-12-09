package net.rubygrapefruit.process

internal class ProcessStartSpec(val commandLine: List<String>)

internal interface ProcessControl {
    fun waitFor()
}

internal expect fun start(spec: ProcessStartSpec): ProcessControl
