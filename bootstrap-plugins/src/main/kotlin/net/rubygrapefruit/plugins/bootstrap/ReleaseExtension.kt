package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.provider.Property

interface ReleaseExtension {
    val nextVersion: Property<String>
}