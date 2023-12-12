package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Distribution
import net.rubygrapefruit.plugins.app.UiApplication
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

abstract class DefaultUiApplication(objects: ObjectFactory, providers: ProviderFactory) : MutableApplication, UiApplication {
    private val dist: DefaultDistribution = objects.newInstance(DefaultDistribution::class.java, "main", true, HostMachine.current is MacOS)

    final override val distributionContainer = SimpleContainer<DefaultDistribution>()

    override val distribution: Provider<Distribution> = providers.provider { dist }

    override val distributions: Provider<List<Distribution>> = providers.provider { listOf(dist) }

    val capitalizedAppName: Provider<String> = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName: Provider<String> = capitalizedAppName.map { "$it.icns" }

    init {
        distributionContainer.add(dist)
    }
}