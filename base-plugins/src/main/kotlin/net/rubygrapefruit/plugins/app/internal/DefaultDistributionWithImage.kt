package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.AbstractDistributionImage
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import javax.inject.Inject


abstract class DefaultDistributionWithImage @Inject constructor(
    name: String,
    canBuildOnHostMachine: Boolean,
    val distTask: TaskProvider<DistributionImage>,
    defaultDist: Provider<Distribution>,
    factory: ObjectFactory,
) : DefaultMutableDistribution(name, canBuildOnHostMachine, defaultDist, factory), BuildableDistribution, HasDistributionImage {

    override val distProducer: TaskProvider<AbstractDistributionImage>
        get() = distTask as TaskProvider<AbstractDistributionImage>

    override fun withImage(action: DistributionImage.() -> Unit) {
        distTask.configure(action)
    }
}
