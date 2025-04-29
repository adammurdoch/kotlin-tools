package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject


abstract class DefaultDistributionWithImage @Inject constructor(
    name: String,
    canBuildOnHostMachine: Boolean,
    override val distTask: TaskProvider<DistributionImage>,
) : DefaultMutableDistribution(name, canBuildOnHostMachine), BuildableDistribution, HasDistributionImage {
    override fun withImage(action: DistributionImage.() -> Unit) {
        distTask.configure(action)
    }
}
