package net.rubygrapefruit.plugins.stage2

import org.gradle.api.provider.Property

abstract class JavaLibrary {
    abstract val targetJavaVersion: Property<Int>
}