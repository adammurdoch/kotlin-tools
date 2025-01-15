package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.provider.Provider

internal class ProjectVersion(val version: Provider<String>) {
    override fun toString(): String {
        return version.get()
    }
}