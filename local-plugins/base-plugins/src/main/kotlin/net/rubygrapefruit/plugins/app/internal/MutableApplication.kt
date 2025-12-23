package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.Installation
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

interface MutableApplication : Application {
    val distributionContainer: DistributionContainer

    override val distributions: Provider<List<Distribution>>
        get() = distributionContainer.distributions

    override val devDistribution: Provider<Distribution>
        get() = distributionContainer.dev

    override val releaseDistribution: Provider<Distribution>
        get() = distributionContainer.release

    override val localInstallation: Property<Installation>

    override val installations: ListProperty<Installation>
}
