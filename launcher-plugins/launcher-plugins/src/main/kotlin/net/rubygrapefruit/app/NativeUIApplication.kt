package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface NativeUIApplication : net.rubygrapefruit.plugins.app.Application, UiApplication {
    /**
     * The name of the NSApplicationDelegate class for the application.
     */
    val delegateClass: Property<String>
}
