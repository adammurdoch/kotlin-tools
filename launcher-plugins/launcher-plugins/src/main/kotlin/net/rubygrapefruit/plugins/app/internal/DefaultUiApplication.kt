package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.UiApplication
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory

abstract class DefaultUiApplication(
    objects: ObjectFactory,
    providers: ProviderFactory,
    project: Project
) : MutableApplication, UiApplication {
    final override val distributionContainer = DistributionContainer(project.tasks, objects, providers)

    val capitalizedAppName: Provider<String> = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName: Provider<String> = capitalizedAppName.map { "$it.icns" }
}