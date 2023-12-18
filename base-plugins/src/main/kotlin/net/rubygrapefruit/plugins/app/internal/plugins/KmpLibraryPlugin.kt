package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.MultiPlatformLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project

class KmpLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(KmpBaseLibraryPlugin::class.java)

            val lib = extensions.getByType(MultiPlatformLibrary::class.java)
            lib.jvm()
            lib.browser()
            lib.nativeDesktop()
        }
    }
}