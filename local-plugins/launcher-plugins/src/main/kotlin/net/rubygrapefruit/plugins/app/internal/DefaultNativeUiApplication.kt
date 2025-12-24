package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.NativeUIApplication
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    componentRegistry: MultiPlatformComponentRegistry,
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultUiApplication(objects, providers, project), MutableNativeApplication, NativeUIApplication {
    private val appTargets = NativeApplicationTargets(componentRegistry, objects, project)

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        config(appTargets.macOS())
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common(config)
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.test(config)
    }
}