package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.Application

interface MutableApplication : Application {
    override val distribution: DefaultDistribution
}
