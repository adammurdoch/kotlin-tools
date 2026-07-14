package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeApplication
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeCliApplication @Inject constructor(
    private val componentRegistry: MultiPlatformComponentRegistry,
    objects: ObjectFactory,
    providers: ProviderFactory,
    componentFactory: ComponentFactory,
    project: Project
) : MutableMultiPlatformApplication, NativeApplication, HasTargets {
    private val appTargets = NativeApplicationTargets(objects, componentFactory, generatedSource)

    override val distributionContainer = DistributionContainer(project.tasks, objects, providers)

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        appTargets.visitPlatforms(consumer)
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        register(OperatingSystem.MacOS).config()
    }

    override fun nativeDesktop() {
        for (os in OperatingSystem.desktop) {
            register(os)
        }
    }

    private fun register(operatingSystem: OperatingSystem): DefaultNativeOsComponent {
        componentRegistry.forOperatingSystem(operatingSystem)
        return appTargets.forOperatingSystem(operatingSystem)
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common.main.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.common.test.dependencies.config()
    }
}
