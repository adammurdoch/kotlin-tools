package net.rubygrapefruit.plugins.stage2

internal interface SampleContainer {
    fun <T : Sample> add(sample: T): T
}