package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.provider.Property

internal class ProjectVersion(val version: Property<String>) {
    override fun toString(): String {
        return version.get()
    }
}