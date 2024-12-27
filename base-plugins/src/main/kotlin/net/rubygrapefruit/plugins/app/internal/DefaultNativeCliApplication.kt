package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.*
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import javax.inject.Inject

abstract class DefaultNativeCliApplication @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    private val objects: ObjectFactory,
    providers: ProviderFactory,
    private val project: Project
) : MutableApplication, MutableNativeApplication, NativeApplication {

    val targets = NativeTargetsContainer(objects, providers, project.tasks)

    override val distributionContainer = targets.distributions

    override val executables: Provider<List<NativeExecutable>>
        get() = targets.executables

    override fun macOS() {
        componentRegistry.macOS { register(it) }
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        macOS()
        val component = objects.newInstance(DefaultNativeComponent::class.java, project, "macosMain")
        config(component)
        component.attach()
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop { register(it) }
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        targets.add(target, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
    }

    override fun common(config: Dependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonMain").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }

    override fun test(config: Dependencies.() -> Unit) {
        project.kotlin.sourceSets.getByName("commonTest").dependencies { config(KotlinHandlerBackedDependencies(this)) }
    }
}
