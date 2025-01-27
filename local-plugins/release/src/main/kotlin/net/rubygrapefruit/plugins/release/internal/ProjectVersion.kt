package net.rubygrapefruit.plugins.release.internal

import net.rubygrapefruit.plugins.lifecycle.internal.VersionNumber
import org.gradle.api.provider.Provider

internal class ProjectVersion(val version: Provider<VersionNumber>) {
    override fun toString(): String {
        return version.get().toString()
    }
}