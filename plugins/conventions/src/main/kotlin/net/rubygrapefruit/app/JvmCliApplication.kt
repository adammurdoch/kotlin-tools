package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface JvmCliApplication : CliApplication {
    val mainClass: Property<String>
}
