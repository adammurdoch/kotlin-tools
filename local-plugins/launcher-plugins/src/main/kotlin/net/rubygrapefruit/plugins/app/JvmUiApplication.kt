package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface JvmUiApplication : JvmApplication, UiApplication {
    val startOnMainThread: Property<Boolean>
}