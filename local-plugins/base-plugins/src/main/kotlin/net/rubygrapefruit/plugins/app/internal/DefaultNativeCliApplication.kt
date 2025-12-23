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
) : MutableApplication, MutableNativeApplication, NativeApplication {
    val targets = NativeTargetsContainer(objects, providers, project.tasks)
    private val appTargets = NativeApplicationTargets(componentRegistry, objects, project)

    override val distributionContainer = targets.distributions

    override val executables: Provider<List<NativeExecutable>>
        get() = targets.executables

    override fun macOS() {
        componentRegistry.macOS { register(it) }
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        macOS()
        config(appTargets.macOS())
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop { register(it) }
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        targets.add(target, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common(config)
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.test(config)
    }

    fun attach() {
        appTargets.mainSourceSet.kotlin.srcDirs(generatedSource)
    }
}
