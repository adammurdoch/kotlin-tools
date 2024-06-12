package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskContainer

class DistributionContainer(private val tasks: TaskContainer, private val objects: ObjectFactory, providers: ProviderFactory) {
    private val distContainer = SimpleContainer<DefaultDistribution>()

    val distributions: Provider<List<Distribution>> = providers.provider { distContainer.all }

    val distribution: Provider<Distribution> = providers.provider { distContainer.all.find { it.isDefault } }

    fun add(name: String, isDefault: Boolean, canBuildForHostMachine: Boolean): DefaultDistribution {
        val distTask = tasks.register("dist${name.capitalize()}", DistributionImage::class.java)
        val dist = objects.newInstance(DefaultDistribution::class.java, name, isDefault, canBuildForHostMachine, distTask)
        distContainer.add(dist)
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