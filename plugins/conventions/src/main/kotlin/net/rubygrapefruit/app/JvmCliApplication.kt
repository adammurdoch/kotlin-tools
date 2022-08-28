package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface JvmCliApplication : CliApplication {
    val module: Property<String>

    val mainClass: Property<String>
}
