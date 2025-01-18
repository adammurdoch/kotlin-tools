package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.provider.Property

interface ReleaseExtension {
    val description: Property<String>

    val nextVersion: Property<String>
}