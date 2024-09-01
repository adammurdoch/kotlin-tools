package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskContainer

class DistributionContainer(private val tasks: TaskContainer, private val objects: ObjectFactory, providers: ProviderFactory) {
    private val distContainer = SimpleContainer<DefaultDistribution>()

    val distributions: Provider<List<Distribution>> = providers.provider { distContainer.all }

    val distribution: Property<Distribution> = objects.property(Distribution::class.java)

    fun add(name: String, isDefault: Boolean, canBuildForHostMachine: Boolean, targetMachine: NativeMachine?, buildType: BuildType): DefaultDistribution {
        val distTask = tasks.register(DefaultDistribution.taskName(name, "dist"), DistributionImage::class.java)
        val dist = if (targetMachine != null) {
            objects.newInstance(DefaultDistribution::class.java, name, canBuildForHostMachine, targetMachine, buildType, distTask, distribution)
        } else {
            objects.newInstance(DefaultPlatformIndependentDistribution::class.java, name, canBuildForHostMachine, buildType, distTask, distribution)
        }
        distContainer.add(dist)
        if (isDefault) {
            distribution.set(dist)
        }
        return dist
    }

    fun each(action: (DefaultDistribution) -> Unit) {
        distContainer.each(action)
    }

    fun eachImage(action: DistributionImage.() -> Unit) {
        distContainer.each { dist ->
            dist.distTask.configure {
                action(it)
            }
        }
    }
}