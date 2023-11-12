package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.UiApplication
import org.gradle.api.provider.Provider

abstract class DefaultUiApplication : MutableApplication, UiApplication {
    val capitalizedAppName: Provider<String> = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName: Provider<String> = capitalizedAppName.map { "$it.icns" }
}