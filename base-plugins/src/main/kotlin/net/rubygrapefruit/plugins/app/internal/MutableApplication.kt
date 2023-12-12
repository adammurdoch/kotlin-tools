package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.Distribution
import org.gradle.api.provider.Provider

interface MutableApplication : Application {
    val distributionContainer: DistributionContainer

    override val distributions: Provider<List<Distribution>>
        get() = distributionContainer.distributions

    override val distribution: Provider<Distribution>
        get() = distributionContainer.distribution
}
