package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.AbstractDistributionImage
import org.gradle.api.provider.Provider

interface BuildableDistribution : Distribution {
    val distTask: Provider<out AbstractDistributionImage>
}