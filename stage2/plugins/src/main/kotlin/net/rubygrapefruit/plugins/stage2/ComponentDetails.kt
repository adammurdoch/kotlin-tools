package net.rubygrapefruit.plugins.stage2

import org.gradle.api.provider.Property

abstract class ComponentDetails {
    abstract val nextVersion: Property<String>
}