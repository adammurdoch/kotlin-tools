package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import net.rubygrapefruit.plugins.app.NativeUIApplication
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
import javax.inject.Inject

abstract class DefaultNativeUiApplication @Inject constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : DefaultUiApplication(objects, providers, project), NativeUIApplication, HasTargets {
    private val appTargets = NativeApplicationTargets(objects)
    private val commonMain = DefaultHasDependencies("commonMain")
    private val commonTest = DefaultHasDependencies("commonTest")
    private val common = DefaultPlatformContribution(commonTest, commonTest)

    override fun visitPlatforms(consumer: (PlatformContribution) -> Unit) {
        consumer(common)
        appTargets.visitTargets(consumer)
    }

    override fun macOS(config: NativeComponent<Dependencies>.() -> Unit) {
        appTargets.macOS().config()
    }

    override fun common(config: Dependencies.() -> Unit) {
        commonMain.dependencies.config()
    }

    override fun test(config: Dependencies.() -> Unit) {
        commonTest.dependencies.config()
    }
}