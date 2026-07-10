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
    private val appTargets = NativeApplicationTargets(objects)
    private val commonMain = DefaultSourceSet("commonMain", DefaultDependencies(), generatedSource)
    private val commonTest = DefaultHasDependencies("commonTest")
    private val common = DefaultPlatformContribution(commonMain, commonTest)

    override val distributionContainer
        get() = targets.distributions

    override val executables: Provider<List<NativeExecutable>>
        get() = targets.executables

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        consumer(common)
        appTargets.visitTargets(consumer)
    }

    override fun macOS() {
        componentRegistry.macOS { register(it) }
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        macOS()
        appTargets.macOS().config()
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop { register(it) }
    }

    private fun KotlinNativeBinaryContainer.register(target: NativeMachine) {
        executable()
        targets.add(target, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
    }

    override fun common(config: Dependencies.() -> Unit) {
        commonMain.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        commonTest.dependencies.config()
    }
}
