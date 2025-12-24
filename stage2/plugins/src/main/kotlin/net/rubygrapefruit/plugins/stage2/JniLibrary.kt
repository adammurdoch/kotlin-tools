package net.rubygrapefruit.plugins.stage2

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

abstract class JniLibrary {
    abstract val targetJavaVersion: Property<Int>

    abstract val cSourceDirs: ConfigurableFileCollection
}