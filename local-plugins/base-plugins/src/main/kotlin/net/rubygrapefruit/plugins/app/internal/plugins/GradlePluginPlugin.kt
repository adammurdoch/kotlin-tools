package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.PluginBundle
import net.rubygrapefruit.plugins.app.Versions
import net.rubygrapefruit.plugins.app.internal.DefaultPluginBundle
import net.rubygrapefruit.plugins.app.internal.componentRegistry
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

@Suppress("unused")
class GradlePluginPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyBasePlugin()

            plugins.apply("java-gradle-plugin")
            plugins.apply("org.jetbrains.kotlin.jvm")
            plugins.apply(LibraryBasePlugin::class.java)
            plugins.apply(JvmConventionsPlugin::class.java)

            repositories.mavenCentral()
            repositories.gradlePluginPortal()

            dependencies.add("implementation", Versions.libs.coordinates("build-constants"))

            val pluginDevExtension = extensions.getByType(GradlePluginDevelopmentExtension::class.java)
            val bundle = extensions.create(
                PluginBundle::class.java,
                "pluginBundle",
                DefaultPluginBundle::class.java,
                pluginDevExtension
            ) as DefaultPluginBundle
            bundle.targetJvmVersion.set(Versions.plugins.jvm.version)

            componentRegistry.register(bundle)
        }
    }
}