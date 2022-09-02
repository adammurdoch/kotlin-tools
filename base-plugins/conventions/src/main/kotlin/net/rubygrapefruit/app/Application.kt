package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface Application {
    val distribution: Distribution

    val appName: Property<String>
}