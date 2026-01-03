package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

internal fun ComponentIdentifier.stringId(): String {
    return when (this) {
        is ModuleComponentIdentifier -> "module:${group}:${module}"
        is ProjectComponentIdentifier -> "project:${buildTreePath}"
        else -> "opaque:$displayName"
    }
}