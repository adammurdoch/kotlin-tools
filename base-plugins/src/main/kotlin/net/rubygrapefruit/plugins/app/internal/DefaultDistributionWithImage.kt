package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject


abstract class DefaultDistributionWithImage @Inject constructor(
    name: String,
    canBuildOnHostMachine: Boolean,
    override val distTask: TaskProvider<DistributionImage>,
    factory: ObjectFactory,
) : DefaultMutableDistribution(name, canBuildOnHostMachine, factory), BuildableDistribution, HasDistributionImage {
    override fun withImage(action: DistributionImage.() -> Unit) {
        distTask.configure(action)
    }
}
