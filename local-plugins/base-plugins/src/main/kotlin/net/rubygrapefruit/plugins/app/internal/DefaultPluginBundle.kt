package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.LibraryDependencies
import net.rubygrapefruit.plugins.app.PluginBundle
import net.rubygrapefruit.plugins.app.internal.component.MutableComponent
import org.gradle.api.provider.Property
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

internal abstract class DefaultPluginBundle(
    private val pluginDevExtension: GradlePluginDevelopmentExtension
) : PluginBundle, MutableComponent, TopLevelJvmComponent, PlatformContribution {
    abstract override val targetJvmVersion: Property<Int>

    override val main = DefaultLibrarySourceSet("main", generatedSource)

    override fun plugin(id: String, implementation: String) {
        val pluginDeclaration = pluginDevExtension.plugins.create(id)
        pluginDeclaration.id = id
        pluginDeclaration.implementationClass = implementation
    }

    override fun dependencies(config: LibraryDependencies.() -> Unit) {
        main.dependencies.config()
    }
}