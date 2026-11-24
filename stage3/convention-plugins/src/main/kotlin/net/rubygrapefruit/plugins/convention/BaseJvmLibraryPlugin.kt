package net.rubygrapefruit.plugins.convention

import net.rubygrapefruit.plugins.app.JvmLibrary
import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
abstract class BaseJvmLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            plugins.apply(ConventionJvmLibraryPlugin::class.java)

            val library = extensions.getByType(JvmLibrary::class.java)
            library.targetJvmVersion.set(BuildConstants.constants.libs.jvm.base.version)
        }
    }
}