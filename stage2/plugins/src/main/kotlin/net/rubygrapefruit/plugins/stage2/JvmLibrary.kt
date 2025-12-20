package net.rubygrapefruit.plugins.stage2

import org.gradle.api.provider.Property

abstract class JvmLibrary {
    abstract val targetJvmVersion: Property<Int>
}