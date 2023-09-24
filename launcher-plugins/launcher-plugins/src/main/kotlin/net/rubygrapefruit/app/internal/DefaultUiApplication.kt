package net.rubygrapefruit.app.internal

import net.rubygrapefruit.app.UiApplication
import net.rubygrapefruit.plugins.app.internal.MutableApplication

abstract class DefaultUiApplication: MutableApplication, UiApplication {
    val capitalizedAppName = appName.map { it.replaceFirstChar { it.uppercase() } }

    val iconName = capitalizedAppName.map { "$it.icns" }
}