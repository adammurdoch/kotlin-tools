package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.*
import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeCliApplication @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    objects: ObjectFactory,
    providers: ProviderFactory,
    componentFactory: ComponentFactory,
    project: Project
) : MutableMultiPlatformApplication, NativeApplication, HasTargets {
    val targets = NativeTargetsContainer(objects, providers, project.tasks)
    private val appTargets = NativeApplicationTargets(objects, componentFactory, generatedSource)
    private val registered = mutableSetOf<OperatingSystem>()

    override val distributionContainer
        get() = targets.distributions

    override val executables: Provider<List<NativeExecutable>>
        get() = targets.executables

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        appTargets.visitPlatforms(consumer)
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        componentRegistry.macOS()
        appTargets.macOS().config()
        register(OperatingSystem.MacOS)
    }

    override fun nativeDesktop() {
        componentRegistry.nativeDesktop()
        appTargets.nativeDesktop()
        for (os in OperatingSystem.desktop) {
            register(os)
        }
    }

    private fun register(operatingSystem: OperatingSystem) {
        if (!registered.add(operatingSystem)) {
            return
        }
        for (machine in operatingSystem.machines) {
            register(machine)
        }
    }

    private fun register(target: NativeMachine) {
        targets.add(target, listOf(BuildType.Debug, BuildType.Release), DefaultHasLauncherExecutableDistribution::class.java)
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common.main.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.common.test.dependencies.config()
    }
}
