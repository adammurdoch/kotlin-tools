package net.rubygrapefruit.plugins.release

import org.gradle.api.provider.Property

interface ReleaseExtension {
    val description: Property<String>

    val nextVersion: Property<String>
}