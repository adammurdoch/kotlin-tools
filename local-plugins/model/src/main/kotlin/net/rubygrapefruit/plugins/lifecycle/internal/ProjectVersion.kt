package net.rubygrapefruit.plugins.lifecycle.internal

import net.rubygrapefruit.plugins.lifecycle.VersionNumber
import org.gradle.api.provider.Provider

internal class ProjectVersion(val version: Provider<VersionNumber>) {
    override fun toString(): String {
        return version.get().version
    }
}