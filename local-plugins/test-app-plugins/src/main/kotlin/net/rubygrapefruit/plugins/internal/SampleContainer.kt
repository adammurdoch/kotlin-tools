package net.rubygrapefruit.plugins.internal

import java.nio.file.Path

internal interface SampleContainer {
    fun <T : Sample> add(name: String, factory: (String, Path) -> T): T
}