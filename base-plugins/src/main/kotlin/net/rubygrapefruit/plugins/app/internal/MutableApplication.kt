package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application

interface MutableApplication : Application {
    override val distribution: DefaultDistribution

    val canBuildDistributionForHostMachine: Boolean
}
