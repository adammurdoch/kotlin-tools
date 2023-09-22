package net.rubygrapefruit.app.plugins

import net.rubygrapefruit.app.internal.JvmModuleRegistry
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion

class JvmConventionsPlugin : Plugin<Project> {
    companion object {
        fun javaVersion(project: Project, version: Int) {
            project.run {
                plugins.withId("java") {
                    val java = extensions.getByType(JavaPluginExtension::class.java)
                    java.toolchain.languageVersion.set(JavaLanguageVersion.of(version))
                }
            }
        }
    }

    override fun apply(target: Project) {
        with(target) {
            javaVersion(this, Versions.java)
            extensions.create("jvmModules", JvmModuleRegistry::class.java)
        }
    }
}