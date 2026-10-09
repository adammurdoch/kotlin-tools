package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.provider.Provider

interface MutableMultiPlatformApplication : MutableApplication {
    val entryPoint: Provider<String>
}