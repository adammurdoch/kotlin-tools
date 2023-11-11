package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.DefaultMultiPlatformLibrary
import net.rubygrapefruit.plugins.app.internal.multiplatformComponents
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpBaseLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("org.jetbrains.kotlin.multiplatform")
            plugins.apply(LibraryBasePlugin::class.java)

            extensions.create(
                MultiPlatformLibrary::class.java,
                "library",
                DefaultMultiPlatformLibrary::class.java,
                multiplatformComponents,
                objects,
                project
            )
        }
    }
}