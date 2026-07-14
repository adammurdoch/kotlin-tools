package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.NativeUIApplication
import net.rubygrapefruit.plugins.app.internal.component.ComponentFactory
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    componentFactory: ComponentFactory,
    project: Project
) : DefaultUiApplication(objects, providers, project), NativeUIApplication, MutableMultiPlatformApplication, HasTargets {
    private val appTargets = NativeApplicationTargets(objects, componentFactory, generatedSource)

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        appTargets.visitPlatforms(consumer)
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        appTargets.forOperatingSystem(OperatingSystem.MacOS).config()
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common.main.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.common.test.dependencies.config()
    }
}