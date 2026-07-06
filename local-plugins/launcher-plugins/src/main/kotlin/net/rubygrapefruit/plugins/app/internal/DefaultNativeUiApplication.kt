package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.NativeUIApplication
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultUiApplication(objects, providers, project), NativeUIApplication, HasTargets {
    private val appTargets = NativeApplicationTargets(objects, project)

    override val common: DefaultDependencies
        get() = appTargets.common

    override val test: DefaultDependencies
        get() = appTargets.test

    override fun visitTargets(consumer: (MutableComponent) -> Unit) {
        appTargets.visitTargets(consumer)
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        appTargets.macOS().config()
    }

    override fun common(config: Dependencies.() -> Unit) {
        appTargets.common.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        appTargets.test.config()
    }
}