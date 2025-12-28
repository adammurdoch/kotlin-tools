package net.rubygrapefruit.plugins.internal

internal interface SampleContainer {
    fun <T : Sample> add(sample: T): T
}