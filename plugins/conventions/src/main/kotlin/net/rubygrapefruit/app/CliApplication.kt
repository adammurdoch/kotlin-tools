package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface CliApplication {
    val distribution: Property<Distribution>
}