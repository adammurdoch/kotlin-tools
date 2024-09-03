package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage

interface MutableDistribution {
    fun withImage(action: DistributionImage.() -> Unit)
}