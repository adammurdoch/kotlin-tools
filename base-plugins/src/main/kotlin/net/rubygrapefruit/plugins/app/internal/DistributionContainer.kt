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

    /**
     * Adds a platform independent distribution.
     */
    fun <T : DefaultDistribution> add(name: String, isDefault: Boolean, type: Class<T>): T {
        val distTask = tasks.register(DefaultDistribution.taskName(name, "dist"), DistributionImage::class.java)
        val dist = objects.newInstance(type, name, HostMachine.current.canBeBuilt, distTask, distribution)
        addDist(dist, isDefault)
        return dist
    }

    /**
     * Adds a platform-dependent distribution.
     */
    fun <T : DefaultDistribution> add(name: String, isDefault: Boolean, canBuildForHostMachine: Boolean, targetMachine: NativeMachine, buildType: BuildType, type: Class<T>): T {
        val distTask = tasks.register(DefaultDistribution.taskName(name, "dist"), DistributionImage::class.java)
        val dist = objects.newInstance(type, name, canBuildForHostMachine, targetMachine, buildType, distTask, distribution)
        addDist(dist, isDefault)
        return dist
    }

    private fun addDist(dist: DefaultDistribution, isDefault: Boolean) {
        distContainer.add(dist)
        if (isDefault && dist.canBuildOnHostMachine) {
            distribution.set(dist)
        }
    }

    fun each(action: (DefaultDistribution) -> Unit) {
        distContainer.each(action)
    }

    inline fun <reified T> eachOfType(crossinline action: T.() -> Unit) {
        each { dist ->
            if (dist is T) {
                action(dist)
            }
        }
    }
}