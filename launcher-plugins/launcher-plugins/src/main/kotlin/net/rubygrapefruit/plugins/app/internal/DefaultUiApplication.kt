package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.UiApplication

abstract class DefaultUiApplication: MutableApplication, UiApplication {
    val capitalizedAppName = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName = capitalizedAppName.map { "$it.icns" }
}