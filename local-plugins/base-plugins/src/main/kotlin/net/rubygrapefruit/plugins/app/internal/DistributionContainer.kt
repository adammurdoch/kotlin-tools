package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.BuildType
import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.tasks.AbstractDistributionImage
import net.rubygrapefruit.plugins.app.internal.tasks.DistributionImage
import net.rubygrapefruit.strings.capitalized
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskContainer

class DistributionContainer(private val tasks: TaskContainer, private val objects: ObjectFactory, providers: ProviderFactory) {
    private val distContainer = SimpleContainer<MutableDistribution>()

    val distributions: Provider<List<Distribution>> = providers.provider { distContainer.all }

    val dev: Property<Distribution> = objects.property(Distribution::class.java)

    val release: Property<Distribution> = objects.property(Distribution::class.java)

    /**
     * Adds a platform independent distribution.
     */
    fun <T : MutableDistribution> add(name: String, isMainDist: Boolean, type: Class<T>): T {
        val distTask = tasks.register(DefaultMutableDistribution.taskName(name, "dist"), DistributionImage::class.java)
        val buildable = HostMachine.current.canBeBuilt
        val dist = objects.newInstance(type, name, buildable, distTask)
        addDist(dist, isMainDist, isMainDist)
        return dist
    }

    /**
     * Adds a platform-dependent distribution.
     *
     * Distribution will not be considered buildable if the Kotlin target cannot be built on the host or if the tooling cannot run on this host.
     * Distribution will be considered the dev distribution if it can be built, targets the host, and is a developer distribution.
     * Distribution will be considered the release distribution if it can be built, targets the host, and is a developer distribution.
     *
     * @param isDevDist Is the distribution a developer distribution?
     * @param isReleaseDist Is the distribution a release distribution?
     * @param canBuildForHostMachine Can the tooling for this distribution run on this host?
     */
    fun <T : MutableDistribution> add(
        baseName: String?,
        isDevDist: Boolean,
        isReleaseDist: Boolean,
        canBuildForHostMachine: Boolean,
        targetMachine: NativeMachine,
        buildType: BuildType,
        type: Class<T>,
        taskType: Class<out AbstractDistributionImage> = DistributionImage::class.java
    ): T {
        val name = targetMachine.kotlinTarget + if (baseName == null) "" else baseName.capitalized()
        distContainer.all.forEach { distribution ->
            if (distribution.name == name) {
                throw IllegalArgumentException("Multiple distributions with name '$name'")
            }
        }
        val distTask = tasks.register(DefaultMutableDistribution.taskName(name, "dist"), taskType)
        val buildable = HostMachine.current.canBeBuilt && HostMachine.current.canBuild(targetMachine) && canBuildForHostMachine
        val isDev = isDevDist && HostMachine.current.machine == targetMachine
        val isRelease = isReleaseDist && HostMachine.current.machine == targetMachine
        val dist = objects.newInstance(type, name, buildable, targetMachine, buildType, distTask)
        addDist(dist, isDev, isRelease)
        return dist
    }

    private fun addDist(dist: MutableDistribution, isDev: Boolean, isRelease: Boolean) {
        distContainer.add(dist)
        if (isDev && dist.canBuildOnHostMachine) {
            dev.set(dist)
        }
        if (isRelease && dist.canBuildOnHostMachine) {
            release.set(dist)
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