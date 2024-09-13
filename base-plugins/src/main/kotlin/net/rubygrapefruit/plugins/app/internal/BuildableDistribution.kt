package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.provider.Provider

interface BuildableDistribution : Distribution {
    val distProducer: Provider<Any>
}