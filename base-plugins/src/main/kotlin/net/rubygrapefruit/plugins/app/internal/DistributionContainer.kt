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
    private val distContainer = SimpleContainer<MutableDistribution>()

    val distributions: Provider<List<Distribution>> = providers.provider { distContainer.all }

    val distribution: Property<Distribution> = objects.property(Distribution::class.java)

    /**
     * Adds a platform independent distribution.
     */
    fun <T : MutableDistribution> add(name: String, isDefault: Boolean, type: Class<T>): T {
        val distTask = tasks.register(DefaultMutableDistribution.taskName(name, "dist"), DistributionImage::class.java)
        val dist = objects.newInstance(type, name, HostMachine.current.canBeBuilt, distTask, distribution)
        addDist(dist, isDefault)
        return dist
    }

    /**
     * Adds a platform-dependent distribution.
     */
    fun <T : MutableDistribution> add(
        baseName: String?,
        isDefault: Boolean,
        canBuildForHostMachine: Boolean,
        targetMachine: NativeMachine,
        buildType: BuildType,
        type: Class<T>
    ): T {
        val name = targetMachine.kotlinTarget + if (baseName == null) "" else baseName.capitalize()
        distContainer.all.forEach { distribution ->
            if (distribution.name == name) {
                throw IllegalArgumentException("Multiple distributions with name '$name'")
            }
        }
        val distTask = tasks.register(DefaultMutableDistribution.taskName(name, "dist"), DistributionImage::class.java)
        val dist = objects.newInstance(type, name, canBuildForHostMachine, targetMachine, buildType, distTask, distribution)
        addDist(dist, isDefault)
        return dist
    }

    private fun addDist(dist: MutableDistribution, isDefault: Boolean) {
        distContainer.add(dist)
        if (isDefault && dist.canBuildOnHostMachine) {
            distribution.set(dist)
        }
    }

    fun each(action: MutableDistribution.() -> Unit) {
        distContainer.each(action)
    }

    inline fun <reified T> eachOfType(crossinline action: T.() -> Unit) {
        each {
            if (this is T) {
                action()
            }
        }
    }
}