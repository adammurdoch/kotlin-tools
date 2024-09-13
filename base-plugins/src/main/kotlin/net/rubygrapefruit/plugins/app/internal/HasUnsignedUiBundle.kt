package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType

interface HasUnsignedUiBundle : MutableDistribution, HasTargetMachine, HasDistributionImage {
    val buildType: BuildType
}