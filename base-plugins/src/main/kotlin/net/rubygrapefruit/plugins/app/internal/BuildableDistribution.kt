package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.AbstractDistributionImage
import org.gradle.api.tasks.TaskProvider

interface BuildableDistribution : Distribution {
    val distTask: TaskProvider<out AbstractDistributionImage>
}