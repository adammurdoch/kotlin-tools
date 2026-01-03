package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.PluginBundle
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

internal abstract class DefaultPluginBundle(private val pluginDevExtension: GradlePluginDevelopmentExtension) : PluginBundle {
    override fun plugin(id: String, implementation: String) {
        val pluginDeclaration = pluginDevExtension.plugins.create(id)
        pluginDeclaration.id = id
        pluginDeclaration.implementationClass = implementation
    }
}