package net.rubygrapefruit.app

import org.gradle.api.provider.Property

interface JvmComponent {
    /**
     * The target Java version for this component.
     */
    val targetJavaVersion: Property<Int>
}