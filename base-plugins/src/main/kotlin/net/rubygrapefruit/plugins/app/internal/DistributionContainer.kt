package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskContainer

class DistributionContainer(private val tasks: TaskContainer, private val objects: ObjectFactory, private val providers: ProviderFactory) {
    private val dists = SimpleContainer<DefaultDistribution>()

    val distributions: Provider<List<Distribution>> = providers.provider { dists.all }

    val distribution: Provider<Distribution> = providers.provider { dists.all.find { it.isDefault } }

    fun add(name: String, isDefault: Boolean, canBuildForHostMachine: Boolean): DefaultDistribution {
        val taskName = if (isDefault) "dist" else "dist-$name"
        val distTask = tasks.register(taskName, DistributionImage::class.java)
        val dist = objects.newInstance(DefaultDistribution::class.java, name, isDefault, canBuildForHostMachine, distTask)
        dists.add(dist)
        return dist
    }

    fun each(action: (DefaultDistribution) -> Unit) {
        dists.each(action)
    }
}