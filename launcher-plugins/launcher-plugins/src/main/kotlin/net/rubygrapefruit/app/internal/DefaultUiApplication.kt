package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.UiApplication

abstract class DefaultUiApplication: MutableApplication, UiApplication {
    val capitalizedAppName = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName = capitalizedAppName.map { "$it.icns" }
}