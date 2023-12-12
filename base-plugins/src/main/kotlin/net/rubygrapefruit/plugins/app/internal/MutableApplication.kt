package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Application

interface MutableApplication : Application {
    val distributionContainer: SimpleContainer<DefaultDistribution>
}
