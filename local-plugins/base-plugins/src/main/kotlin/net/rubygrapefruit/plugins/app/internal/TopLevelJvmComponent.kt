package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.provider.Provider

interface TopLevelJvmComponent {
    val targetJvmVersion: Provider<Int>
}