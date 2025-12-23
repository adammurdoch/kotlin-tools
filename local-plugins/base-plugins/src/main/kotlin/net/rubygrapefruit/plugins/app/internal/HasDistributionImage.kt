package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.tasks.TaskProvider

interface HasDistributionImage : MutableDistribution {
    override val distTask: TaskProvider<DistributionImage>

    fun withImage(action: DistributionImage.() -> Unit)
}