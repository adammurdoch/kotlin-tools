package net.rubygrapefruit.plugins.app

import org.gradle.api.provider.Property

interface NativeUIApplication : Application, UiApplication, MultiPlatformComponent<Dependencies> {
    /**
     * The name of the NSApplicationDelegate class for the application.
     */
    val delegateClass: Property<String>
}
