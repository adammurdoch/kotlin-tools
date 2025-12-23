package net.rubygrapefruit.plugins.app.internal.plugins

import net.rubygrapefruit.plugins.app.NativeApplication
import org.gradle.api.Plugin
import org.gradle.api.Project

open class NativeCliApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply(NativeCliApplicationBasePlugin::class.java)

            val application = extensions.getByType(NativeApplication::class.java)
            application.nativeDesktop()
        }
    }
}
