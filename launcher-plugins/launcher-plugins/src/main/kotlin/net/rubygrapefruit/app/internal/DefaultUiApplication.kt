package net.rubygrapefruit.app.internal

abstract class DefaultUiApplication: MutableApplication {
    val capitalizedAppName = appName.map { it.replaceFirstChar { it.uppercase() } }
}