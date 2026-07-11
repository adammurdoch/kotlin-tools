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
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : MutableApplication, NativeApplication, HasTargets {
    val targets = NativeTargetsContainer(objects, providers, project.tasks)
    private val appTargets = NativeApplicationTargets(objects, generatedSource)

    override val distributionContainer
        get() = targets.distributions

    override val executables: Provider<List<NativeExecutable>>
        get() = targets.executables

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        appTargets.visitPlatforms(consumer)
    }

    override fun macOS() {
        macOS { }
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        componentRegistry.macOS { register(it) }
        appTargets.macOS().config()
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop { register(it) }
        appTargets.nativeDesktop()
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        targets.add(target, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common.main.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.common.test.dependencies.config()
    }
}
