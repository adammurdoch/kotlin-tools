package net.rubygrapefruit.plugins.stage2

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

abstract class JniLibrary {
    /**
     * Java version for the Java part of the library.
     */
    abstract val targetJavaVersion: Property<Int>

    abstract val cSourceDirs: ConfigurableFileCollection
}